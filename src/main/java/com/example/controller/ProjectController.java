package com.example.controller;

import com.example.apiPayload.ApiResponse;
import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.code.status.SuccessStatus;
import com.example.domain.User;
import com.example.dto.PrincipleDetail;
import com.example.dto.ProjectDTO;
import com.example.dto.request.ProjectRequestDTO;
import com.example.dto.request.UpdateProjectRequestDTO;
import com.example.dto.response.ProjectResponseDTO;
import com.example.service.ProjectService;
import com.example.service.ProjectUserService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectUserService projectUserService;
    private final UserService userService;

    private final SimpMessagingTemplate messagingTemplate;

    //프로젝트 생성
    @PostMapping("/project")
    public ApiResponse<?> newProject(@AuthenticationPrincipal PrincipleDetail principleDetail,
                                          @RequestBody ProjectRequestDTO projectRequestDTO) {
        if (principleDetail == null) {
            log.info("Unauthenticated request - User not logged in");
            return ApiResponse.onFailure(
                    ErrorStatus.USER_NOT_LOGIN.getCode(),
                    ErrorStatus.USER_NOT_LOGIN.getMessage(),
                    "로그인을 해야 됩니다."
            );
        }

        User user = userService.loadMemberByPrincipleDetail(principleDetail);

        Long projectId = projectService.saveProject(projectRequestDTO, user);

        return ApiResponse.onSuccess(SuccessStatus.CREATED.getCode(), SuccessStatus.CREATED.getMessage(), projectId);
    }

    // 전체 프로젝트 보기
    @GetMapping("/project")
    public ApiResponse<?> projects(@AuthenticationPrincipal PrincipleDetail principleDetail) {

        if (principleDetail == null) {
            log.info("Unauthenticated request - User not logged in");
            return ApiResponse.onFailure(
                    ErrorStatus.USER_NOT_LOGIN.getCode(),
                    ErrorStatus.USER_NOT_LOGIN.getMessage(),
                    "로그인을 해야 됩니다."
            );
        }

        // 사용자 정보 로드
        User user = userService.loadMemberByPrincipleDetail(principleDetail);

        // 사용자가 참여한 모든 프로젝트 조회
        List<ProjectDTO> allProjectsList = projectUserService.findAllProjects(user);

        return ApiResponse.onSuccess(
                SuccessStatus.OK.getCode(),
                SuccessStatus.OK.getMessage(),
                allProjectsList
        );
    }


    // 선택 프로젝트 보기
    @GetMapping("/project/{projectId}")
    public ApiResponse<?> viewProject(@AuthenticationPrincipal PrincipleDetail principleDetail,
                                                @PathVariable("projectId") Long projectId) {

        if (principleDetail == null) {
            log.info("Unauthenticated request - User not logged in");
            return ApiResponse.onFailure(
                    ErrorStatus.USER_NOT_LOGIN.getCode(),
                    ErrorStatus.USER_NOT_LOGIN.getMessage(),
                    "로그인을 해야 됩니다."
            );
        }

        User user = userService.loadMemberByPrincipleDetail(principleDetail);
        ProjectResponseDTO projectResponseDTO = projectService.viewProjectByIdAndUser(projectId, user);

        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), projectResponseDTO);
    }

    // 프로젝트 정보 편집
    @PatchMapping("/project/{projectId}")
    public ApiResponse<String> editProject(@AuthenticationPrincipal PrincipleDetail principleDetail,
                                           @PathVariable("projectId") Long projectId,
                                           @RequestBody UpdateProjectRequestDTO updateProjectRequestDTO) {

        if (principleDetail == null) {
            log.info("Unauthenticated request - User not logged in");
            return ApiResponse.onFailure(
                    ErrorStatus.USER_NOT_LOGIN.getCode(),
                    ErrorStatus.USER_NOT_LOGIN.getMessage(),
                    "로그인을 해야 됩니다."
            );
        }

        User user = userService.loadMemberByPrincipleDetail(principleDetail);
        projectService.updateProject(projectId, user, updateProjectRequestDTO);

        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(),"Project updated");
    }

}
