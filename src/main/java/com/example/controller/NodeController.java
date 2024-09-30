package com.example.controller;

import com.example.apiPayload.ApiResponse;
import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.code.status.SuccessStatus;
import com.example.domain.Project;
import com.example.domain.User;
import com.example.dto.PrincipleDetail;
import com.example.dto.request.NodeRequestDTO;
import com.example.service.ImageService;
import com.example.service.NodeService;
import com.example.service.ProjectService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final ProjectService projectService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/node")
    public ApiResponse<String> newNode(@RequestBody NodeRequestDTO nodeRequestDto,
                                       @AuthenticationPrincipal PrincipleDetail principleDetail) {
        if (principleDetail == null) {
            log.info("Unauthenticated request - User not logged in");
            return ApiResponse.onFailure(
                    ErrorStatus.USER_NOT_LOGIN.getCode(),
                    ErrorStatus.USER_NOT_LOGIN.getMessage(),
                    "로그인을 해야 됩니다."
            );
        }

        User user = userService.loadMemberByPrincipleDetail(principleDetail);
        Project project = projectService.verifyProjectAccess(nodeRequestDto.getProjectId(), user);

        nodeService.saveNode(nodeRequestDto, user, project);

        // 생성된 노드를 실시간으로 브로드캐스트 (WebSocket 사용)
        messagingTemplate.convertAndSend("/topic/nodes/" + nodeRequestDto.getProjectId(), nodeRequestDto);

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
