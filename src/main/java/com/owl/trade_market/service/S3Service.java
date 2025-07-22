package com.owl.trade_market.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * ✅ S3에 파일 업로드 (예외 처리 포함)
     */
    public void uploadFile(String key, File file) {
        System.out.println("☁️ [S3Service] S3 업로드 시도 bucket=" + bucketName + ", key=" + key);

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build(),
                    RequestBody.fromFile(file)
            );
            System.out.println("✅ [S3Service] 업로드 성공!");

        } catch (S3Exception s3e) {
            // AWS S3 API에서 내려온 에러 (권한/버킷 정책/리전 문제 등)
            System.err.println("❌ [S3Service] S3Exception 업로드 실패: " + s3e.awsErrorDetails().errorMessage());
            throw s3e; // 상위 레벨로 다시 던지기 (rollback 유도)

        } catch (SdkClientException sdkEx) {
            // 네트워크 문제 or 자격 증명 문제
            System.err.println("❌ [S3Service] SDK 클라이언트 오류: " + sdkEx.getMessage());
            throw sdkEx;

        } catch (Exception e) {
            // 그 외 예외
            System.err.println("❌ [S3Service] 업로드 중 알 수 없는 오류: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }

    /**
     * S3에서 파일 삭제
     */
    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build()
            );
            System.out.println("✅ [S3Service] 삭제 성공 key=" + key);
        } catch (Exception e) {
            System.err.println("❌ [S3Service] 삭제 실패 key=" + key + ", reason=" + e.getMessage());
            throw e;
        }
    }

    /**
     * ✅ S3의 퍼블릭 URL 생성
     */
    public String getFileUrl(String key) {
        return String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s",
                bucketName, key);
    }

    public String extractKeyFromUrl(String imageUrl) {
        String baseUrl = String.format("https://%s.s3.ap-northeast-2.amazonaws.com/", bucketName);
        return imageUrl.replace(baseUrl, "");
    }

}
