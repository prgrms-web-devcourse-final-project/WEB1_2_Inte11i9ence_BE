package com.prgrmsfinal.skypedia.photo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    //버킷 이름을 application.prop 에서 가져와서 변수를 넣는거임.
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;
    private final S3Presigner s3Presigner;

    //올릴 버킷명 설정 및 경로 지정
    public String createPresignedURL(String filepath) {
        var putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(filepath)
                .build();
        //url 유효기간 및 put 오브젝트
        var preSignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3))
                .putObjectRequest(putObjectRequest)
                .build();
        return s3Presigner.presignPutObject(preSignRequest).url().toString();
    }

    @Override
    public String getPresignedUrl(String filepath) {
        //파일 패스로 get요청
        var getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(filepath)
                .build();
        var presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30))
                .getObjectRequest(getObjectRequest)
                .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    @Override
    public String modifyPresignedURL(String filepath) {
        var putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(filepath) // 기존 S3FileKey 사용
                .build();

        // 프리사인드 URL 생성
        var preSignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5)) // URL 유효기간 설정
                .putObjectRequest(putObjectRequest)
                .build();

        return s3Presigner.presignPutObject(preSignRequest).url().toString();
    }

    @Override
    public String deletePresignedURL(String filepath) {
        return "";
    }

}
