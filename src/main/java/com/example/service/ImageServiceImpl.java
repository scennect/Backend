package com.example.service;

import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ImageServiceImpl implements ImageService{

    private final RestTemplate restTemplate;

    @Override
    public String generateTextToImage(String prompt) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "https://82d0-124-55-57-87.ngrok-free.app//generate-image";  // FastAPI 서버의 URL

        String requestBody = "{\"prompt\": \"" + prompt + "\"}";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // FastAPI 서버에 POST 요청을 보내 이미지 생성
            log.info("generate-image 요청 시작");
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            log.info("generate-image 요청 완료");
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to generate image: {}", e.getMessage());
            throw new GeneralException(ErrorStatus.IMAGE_GENERATE_FAILURE);
        }
    }

    @Override
    public String generateImageToImage(String prompt, String imageURL) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "https://82d0-124-55-57-87.ngrok-free.app//modify-image";  // FastAPI 서버의 URL

        String requestBody = "{\"prompt\": \"" + prompt + "\", \"imageURL\": " + imageURL + "}";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // FastAPI 서버에 POST 요청을 보내 이미지 생성
            log.info("modify-image 요청 시작");
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            log.info("modify-image 요청 끝");
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to generate image: {}", e.getMessage());
            throw new GeneralException(ErrorStatus.IMAGE_GENERATE_FAILURE);
        }
    }


}
