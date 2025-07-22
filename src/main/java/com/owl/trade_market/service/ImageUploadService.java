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

    @Transactional
    public Image uploadProductImage(Long productId, MultipartFile multipartFile) throws IOException {
        // ✅ 1. 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // ✅ 2. MultipartFile → 임시 파일 변환
        String originalFilename = multipartFile.getOriginalFilename();
        File tempFile = File.createTempFile("upload-", originalFilename);
        multipartFile.transferTo(tempFile);

        // ✅ 3. S3에 업로드
        String key = "product/" + productId + "/" + originalFilename; // S3 내 경로
        s3Service.uploadFile(key, tempFile);

        // ✅ 4. S3 URL 생성
        String imageUrl = s3Service.getFileUrl(key);

        // ✅ 5. Image 엔티티 생성 & DB 저장
        Image image = new Image(product, imageUrl);
        return imageRepository.save(image);
    }
}
