package com.example.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.IOException;

public interface ImageService {

    public String generateTextToImage(String prompt);

    public String generateImageToImage(String prompt, String imageURL);

    public void deleteS3Image(String imageURL);

}
