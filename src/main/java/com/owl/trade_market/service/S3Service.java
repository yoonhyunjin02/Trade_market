package com.owl.trade_market.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

    // ✅ 생성자 주입 (Lombok 없이)
    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * ✅ S3에 파일 업로드
     * @param key S3에 저장될 파일명 (경로 포함 가능)
     * @param file 실제 업로드할 File 객체
     */
    public void uploadFile(String key, File file) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build(),
                RequestBody.fromFile(file)
        );
    }

    /**
     * ✅ S3에서 파일 삭제
     * @param key S3에 저장된 파일명
     */
    public void deleteFile(String key) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build()
        );
    }

    /**
     * ✅ S3의 파일 URL 반환 (퍼블릭 버킷일 경우만 바로 접근 가능)
     * @param key S3에 저장된 파일명
     * @return 파일의 접근 URL
     */
    public String getFileUrl(String key) {
        return String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s",
                bucketName, key);
    }
}
