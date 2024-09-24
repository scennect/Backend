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
    public String generateImage(String text, String imageURL) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // imageURL 없는 경우 : 첫번째 노드 생성시
        if (imageURL.isEmpty()) {
            String url = "http://localhost:8000/generate-text2image";  // FastAPI 서버의 URL

            String requestBody = "{\"text\": \"" + text + "\"}";

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            try {
                // FastAPI 서버에 POST 요청을 보내 이미지 생성
                ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
                return response.getBody();
            } catch (Exception e) {
                log.error("Failed to generate image: {}", e.getMessage());
                throw new GeneralException(ErrorStatus.IMAGE_GENERATE_FAILURE);
            }


        }
        // imageURL 있는 경우
        else {
            String url = "http://localhost:8000/generate-image2image";  // FastAPI 서버의 URL

            String requestBody = "{\"text\": \"" + text + "\"imageURL\": \"" + imageURL + "\"}";

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            try {
                // FastAPI 서버에 POST 요청을 보내 이미지 생성
                ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
                return response.getBody();
            } catch (Exception e) {
                log.error("Failed to generate image: {}", e.getMessage());
                throw new GeneralException(ErrorStatus.IMAGE_GENERATE_FAILURE);
            }

        }
    }
}
