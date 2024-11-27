package com.example.controller;

import com.example.apiPayload.ApiResponse;
import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.code.status.SuccessStatus;
import com.example.domain.Project;
import com.example.domain.User;
import com.example.dto.PrincipleDetail;
import com.example.dto.CoordinateDTO;
import com.example.dto.request.NodeRequestDTO;
import com.example.dto.response.NodeResponseDTO;
import com.example.service.NodeService;
import com.example.service.ProjectService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
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

        NodeResponseDTO nodeResponseDTO = nodeService.saveNode(nodeRequestDto, user, project);

        // 생성된 노드를 /topic/project/{projectId}로 실시간 브로드캐스트 (WebSocket 사용)
        messagingTemplate.convertAndSend("/topic/project/" + nodeRequestDto.getProjectId(), nodeResponseDTO);

        return ApiResponse.onSuccess(SuccessStatus.CREATED.getCode(), SuccessStatus.CREATED.getMessage(), "New node created");
    }

    @PatchMapping("/node/{nodeId}")
    public ApiResponse<String> updateCoordinate(@RequestBody CoordinateDTO coordinateDTO, @AuthenticationPrincipal PrincipleDetail principleDetail,
                                                @PathVariable("nodeId") Long nodeId) {
        if (principleDetail == null) {
            log.info("Unauthenticated request - User not logged in");
            return ApiResponse.onFailure(
                    ErrorStatus.USER_NOT_LOGIN.getCode(),
                    ErrorStatus.USER_NOT_LOGIN.getMessage(),
                    "로그인을 해야 됩니다."
            );
        }

        User user = userService.loadMemberByPrincipleDetail(principleDetail);

        nodeService.updateCoordinate(nodeId, user, coordinateDTO);

        return ApiResponse.onSuccess(SuccessStatus.CREATED.getCode(), SuccessStatus.OK.getMessage(),
                "nodeId : " + nodeId + " 위치가 성공적으로 업데이트 됐습니다.");
    }

    @MessageMapping("/node/{projectId}")
    @SendTo("/topic/node/{projectId}")
    public void moveNode(@DestinationVariable String projectId, @Payload CoordinateDTO coordinateDTO) {

        // 전달할 메시지 형식으로 변환
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setHeader("projectId", projectId);

        // 노드의 ID, x, y를 포함한 응답을 클라이언트에 전송
        messagingTemplate.convertAndSend("/topic/node/" + projectId, coordinateDTO);
    }

    @DeleteMapping("/project/{projectId}/node/{nodeId}")
    public ApiResponse<String> deleteNode(@AuthenticationPrincipal PrincipleDetail principleDetail,
                                          @PathVariable("projectId") Long projectId,
                                          @PathVariable("nodeId") Long nodeId) {

        User user = userService.findUserByUsername(principleDetail.getUsername());

        nodeService.DeleteNodeByIdAndUser(nodeId, user);

        // 노드 삭제 후 해당 프로젝트에 삭제 이벤트 알림
        messagingTemplate.convertAndSend("/topic/delete/" + projectId, nodeId);

        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), "Node successfully deleted");
    }

}
