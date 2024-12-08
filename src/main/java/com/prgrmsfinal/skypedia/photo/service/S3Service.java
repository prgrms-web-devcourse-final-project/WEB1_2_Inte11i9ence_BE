package com.prgrmsfinal.skypedia.photo.service;

public interface S3Service {
	boolean doesObjectExist(String filepath);

	String createPresignedURL(String filepath);

	String getPresignedUrl(String filepath);

	String modifyPresignedURL(String filepath);

	String deletePresignedURL(String filepath);

}