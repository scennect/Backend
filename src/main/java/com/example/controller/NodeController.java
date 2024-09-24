package com.example.controller;

import com.example.apiPayload.ApiResponse;
import com.example.apiPayload.code.status.SuccessStatus;
import com.example.domain.Node;
import com.example.domain.User;
import com.example.dto.PrincipleDetail;
import com.example.dto.request.NodeRequestDTO;
import com.example.jwt.JWTUtil;
import com.example.service.ImageService;
import com.example.service.NodeService;
import com.example.service.UserService;
import com.example.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NodeController {

    private final NodeService nodeService;
    private final UserService userService;
    private final ImageService imageService;
    private final JWTUtil jwtUtil;
    private final UserServiceImpl userServiceImpl;

    @PostMapping("/node")
    public ApiResponse<String> newNode(@RequestBody NodeRequestDTO nodeRequestDto,
                                       @AuthenticationPrincipal PrincipleDetail principleDetail) {
        if (principleDetail == null) {
            log.info("not logged in user");
        } else {
            nodeRequestDto.setUsername(principleDetail.getUsername());
        }

        Long parentNodeId = nodeRequestDto.getParentNodeId();
        if (parentNodeId != null) {
            nodeService.checkParentNode(nodeRequestDto);
        }

        String imageURL = imageService.generateImage(nodeRequestDto.getText(), nodeRequestDto.getParentImageURL());
        //String imageURL = "default_Image_Url"; // 로컬에서 위에 generateImage 없이 돌릴때 사용할 용도
        nodeRequestDto.setImageURL(imageURL);

        nodeService.saveNode(nodeRequestDto);

        return ApiResponse.onSuccess(SuccessStatus.CREATED.getCode(), SuccessStatus.CREATED.getMessage(), "New node created");
    }

    @DeleteMapping("/node/{nodeId}")
    public ApiResponse<String> newNode(@AuthenticationPrincipal PrincipleDetail principleDetail,
                                       @PathVariable("nodeId") Long nodeId) {

        User user = userService.findUserByUsername(principleDetail.getUsername());

        nodeService.DeleteNodeByIdAndUser(nodeId, user);

        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), "Node successfully deleted");
    }

}
