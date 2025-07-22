package com.owl.trade_market.service;

import com.owl.trade_market.entity.Image;
import com.owl.trade_market.entity.Product;
import com.owl.trade_market.repository.ImageRepository;
import com.owl.trade_market.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class ImageUploadService {

    private final S3Service s3Service;
    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;

    public ImageUploadService(S3Service s3Service,
                              ImageRepository imageRepository,
                              ProductRepository productRepository) {
        this.s3Service = s3Service;
        this.imageRepository = imageRepository;
        this.productRepository = productRepository;
    }

    /**
     * 상품 이미지 업로드 & DB 저장
     */
    @Transactional
    public Image uploadProductImage(Long productId, MultipartFile multipartFile) throws IOException {

        System.out.println("📸 [uploadProductImage] START 호출됨 productId=" + productId);

        /* 1) 상품 조회 ------------------------------------------------------- */
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    System.err.println("❌ productRepository.findById 실패! ID=" + productId);
                    return new IllegalArgumentException("상품을 찾을 수 없습니다. ID = " + productId);
                });
        System.out.println("✅ product 조회 성공: title=" + product.getTitle() + ", seller=" + product.getSeller().getUserName());

        /* 2) MultipartFile → 임시 파일 변환 ---------------------------------- */
        String originalFilename = multipartFile.getOriginalFilename();
        System.out.println("📂 originalFilename(raw)=" + originalFilename);

        // ✅ 파일 확장자 추출
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // ✅ 안전한 파일명 생성: UUID + 타임스탬프 + 확장자
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String safeFilename = UUID.randomUUID().toString() + "_" + timestamp + fileExtension;
        System.out.println("📂 safeFilename=" + safeFilename);

        File tempFile = File.createTempFile("upload-", "-" + safeFilename);
        multipartFile.transferTo(tempFile);
        System.out.println("✅ tempFile 생성 완료: " + tempFile.getAbsolutePath() + ", size=" + tempFile.length());

        /* 3) S3 업로드 ------------------------------------------------------- */
        String key = "product/%d/%s".formatted(productId, safeFilename);
        System.out.println("☁️ S3 key=" + key);

        s3Service.uploadFile(key, tempFile);
        System.out.println("✅ S3 업로드 완료");

        /* 4) S3 URL 생성 ----------------------------------------------------- */
        String imageUrl = s3Service.getFileUrl(key);
        System.out.println("🌐 imageUrl=" + imageUrl);

        /* 5) Image 엔티티 저장 & 양방향 컬렉션 동기화 -------------------------- */
        Image image = new Image(product, imageUrl);
        product.getImages().add(image);

        System.out.println("💾 imageRepository.save 호출 직전");
        Image saved = imageRepository.save(image);
        System.out.println("✅ imageRepository.save 완료: imageId=" + saved.getId());

        /* 6) 임시 파일 삭제 --------------------------------------------------- */
        if (!tempFile.delete()) {
            System.err.println("[WARN] tempFile 삭제 실패: " + tempFile.getAbsolutePath());
        } else {
            System.out.println("🗑 tempFile 삭제 완료");
        }

        System.out.println("📸 [uploadProductImage] END");
        return saved;
    }

    @Transactional
    public void replaceProductImage(Long productId, MultipartFile newFile) throws IOException {
        System.out.println("🔄 [replaceProductImage] 시작 productId=" + productId);

        Product product = productRepository.findByIdWithImages(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID = " + productId));

        // ✅ 기존 이미지 삭제 (S3 + DB)
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            System.out.println("🗑 기존 이미지 개수: " + product.getImages().size());
            for (Image img : product.getImages()) {
                // S3 URL → Key 변환
                String key = s3Service.extractKeyFromUrl(img.getImage());
                System.out.println("🗑 S3 이미지 삭제 key=" + key);

                // S3에서 삭제
                s3Service.deleteFile(key);
                // DB에서 삭제
                imageRepository.delete(img);
            }
            // JPA 연관관계 초기화
            product.getImages().clear();
        } else {
            System.out.println("➡ 기존 이미지 없음");
        }

        // ✅ 새 이미지 업로드
        System.out.println("⬆ 새 이미지 업로드 시작");
        uploadProductImage(productId, newFile);

        System.out.println("✅ [replaceProductImage] 완료");
    }
}