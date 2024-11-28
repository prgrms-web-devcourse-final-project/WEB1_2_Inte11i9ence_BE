package com.prgrmsfinal.skypedia.photo.service;

public interface S3Service {
    public String createPresignedURL(String filepath);
    public String getPresignedUrl(String filepath);
    public String modifyPresignedURL(String filepath);
    public String deletePresignedURL(String filepath);

}
