package com.example.controller;

import com.example.apiPayload.ApiResponse;
import com.example.apiPayload.code.status.SuccessStatus;
import com.example.apiPayload.exception.GeneralException;
import com.example.dto.request.NodeRequestDTO;
import com.example.dto.response.NodeResponseDTO;
import com.example.jwt.JWTUtil;
import com.example.service.ImageService;
import com.example.service.NodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.example.apiPayload.code.status.ErrorStatus.IMAGE_UPLOAD_FAILURE;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NodeController {

    private final NodeService nodeService;
    private final ImageService imageService;
    private final JWTUtil jwtUtil;

    @PostMapping("/new-node")
    public ApiResponse<String> newNode(@RequestPart("nodeRequestDto") NodeRequestDTO nodeRequestDto,
                                       @RequestPart("imageFile") MultipartFile imageFile,
                                       @CookieValue(value = "Authorization", required = false) String Authorization) {
        try{
            String username = jwtUtil.getUsername(Authorization);
            nodeRequestDto.setUsername(username);
        } catch (IllegalArgumentException e) {
            log.info("not logged in user");
        }

        try {
            String s3Url = imageService.imageUpload(imageFile);
            nodeRequestDto.setImageURL(s3Url);

        } catch (IOException e) {
            throw new GeneralException(IMAGE_UPLOAD_FAILURE);
        }

        nodeService.saveNode(nodeRequestDto);
        return ApiResponse.onSuccess(SuccessStatus.CREATED.getCode(), SuccessStatus.CREATED.getMessage(), "New node created");
    }



}
