package com.example.controller;

import com.example.apiPayload.ApiResponse;
import com.example.apiPayload.code.status.SuccessStatus;
import com.example.apiPayload.exception.GeneralException;
import com.example.domain.Node;
import com.example.dto.request.NodeRequestDTO;
import com.example.dto.response.NodeResponseDTO;
import com.example.jwt.JWTUtil;
import com.example.service.ImageService;
import com.example.service.NodeService;
import com.example.service.ProjectService;
import com.example.websocket.NodePosition;
import com.example.websocket.NodePositionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/new-node")
    public ApiResponse<String> newNode(@RequestBody NodeRequestDTO nodeRequestDto,
                                       @CookieValue(value = "Authorization", required = false) String Authorization) {
        try {
            String username = jwtUtil.getUsername(Authorization);
            nodeRequestDto.setUsername(username);
        } catch (IllegalArgumentException e) {
            log.info("not logged in user");
        }

        Long saveNodeId = nodeService.saveNode(nodeRequestDto);
        String text = nodeRequestDto.getText();

        String imageURL = imageService.generateImage(text);
        nodeService.updateNodeImageURL(saveNodeId, imageURL);

        return ApiResponse.onSuccess(SuccessStatus.CREATED.getCode(), SuccessStatus.CREATED.getMessage(), "New node created");
    }

    @MessageMapping("/moveNode")
    @SendTo("/topic/nodes")
    public NodePositionDTO updateNode(@Payload NodePositionDTO nodePositionDTO) {

        nodeService.updateNodePosition(nodePositionDTO);

        return nodePositionDTO;
    }

    // '/api/nodes?projectId=1' 형식
    @GetMapping("/api/nodes")
    public List<Node> getNodes(@RequestParam Long projectId) {
        return nodeService.getNodesByProjectId(projectId);
    }

}
