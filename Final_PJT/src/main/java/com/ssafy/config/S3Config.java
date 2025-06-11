package com.ssafy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
	
	@Value("${aws.access-key-id}")
	private String accessKeyId;

	@Value("${aws.secret-access-key}")
	private String secretAccessKeyId;

    @Bean
    public S3Client s3client(){
		Region region = Region.US_EAST_1;
		AwsCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretAccessKeyId);
		return S3Client.builder()
		.region(region)
		.credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
		.build();
	}
}
