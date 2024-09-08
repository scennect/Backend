package com.example.controller;

import com.example.apiPayload.ApiResponse;
import com.example.apiPayload.code.status.SuccessStatus;
import com.example.apiPayload.exception.GeneralException;
import com.example.domain.Node;
import com.example.domain.User;
import com.example.dto.CustomUserDetails;
import com.example.dto.request.NodeRequestDTO;
import com.example.dto.response.NodeResponseDTO;
import com.example.jwt.JWTUtil;
import com.example.service.ImageService;
import com.example.service.NodeService;
import com.example.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NodeController {

    private final NodeService nodeService;
    private final ImageService imageService;
    private final JWTUtil jwtUtil;
    private final UserServiceImpl userServiceImpl;

    @PostMapping("/new-node")
    public ApiResponse<String> newNode(@RequestBody NodeRequestDTO nodeRequestDto,
                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if(customUserDetails == null){
            log.info("not logged in user");
        }
        else{
            nodeRequestDto.setUsername(customUserDetails.getUsername());
        }

        Long parentNodeId = nodeRequestDto.getParentNodeId();
        if(parentNodeId != null){
            nodeService.checkParentNode(nodeRequestDto);
        }

        String imageURL = imageService.generateImage(nodeRequestDto.getText(), nodeRequestDto.getParentImageURL());
        nodeRequestDto.setImageURL(imageURL);

        nodeService.saveNode(nodeRequestDto);

        return ApiResponse.onSuccess(SuccessStatus.CREATED.getCode(), SuccessStatus.CREATED.getMessage(), "New node created");
    }

}
