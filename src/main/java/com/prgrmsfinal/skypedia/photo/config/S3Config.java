package com.prgrmsfinal.skypedia.photo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {
	@Value("${spring.cloud.aws.credentials.accessKey}")
	private String accessKey;

	@Value("${spring.cloud.aws.credentials.secretKey}")
	private String secretKey;

	@Bean
	public S3Presigner presigner() {
		AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
			AwsBasicCredentials.create(accessKey, secretKey)
		);
		return S3Presigner.builder()
			.region(Region.AP_NORTHEAST_2)
			.credentialsProvider(credentialsProvider)
			.build();
	}

	@Bean
	public S3Client s3Client() {
		return S3Client.builder()
			.region(Region.AP_NORTHEAST_2) // 원하는 리전을 설정하세요.
			.build();
	}

}