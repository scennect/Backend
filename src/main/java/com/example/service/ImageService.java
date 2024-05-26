package com.example.service;

import com.example.config.S3Config;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.IOException;

public interface ImageService {
    public String imageUpload(MultipartFile imageFile) throws IOException;

}
