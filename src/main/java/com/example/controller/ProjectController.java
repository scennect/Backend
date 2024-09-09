package com.example.controller;

import com.example.apiPayload.ApiResponse;
import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.code.status.SuccessStatus;
import com.example.domain.Node;
import com.example.domain.Project;
import com.example.domain.User;
import com.example.dto.CustomUserDetails;
import com.example.dto.ProjectDTO;
import com.example.dto.request.ProjectRequestDTO;
import com.example.dto.request.UpdateProjectRequestDTO;
import com.example.dto.response.AllProjectResponseDTO;
import com.example.dto.response.NodeResponseDTO;
import com.example.dto.response.ProjectResponseDTO;
import com.example.jwt.JWTUtil;
import com.example.service.NodeService;
import com.example.service.ProjectService;
import com.example.service.ProjectUserService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProjectController {
    private final JWTUtil jwtUtil;

    private final ProjectService projectService;
    private final ProjectUserService projectUserService;
    private final UserService userService;
    private final NodeService nodeService;

    @PostMapping("/new-project")
    public ApiResponse<String> newProject(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                          @RequestBody ProjectRequestDTO projectRequestDTO) {
        if (customUserDetails == null) {
            return ApiResponse.onFailure(ErrorStatus.USER_NOT_LOGIN.getCode(), ErrorStatus.USER_NOT_LOGIN.getMessage(), "로그인을 해야됩니다.");
        }
        String username = customUserDetails.getUsername();
        projectRequestDTO.setUsername(username);

        projectService.saveProject(projectRequestDTO);

        return ApiResponse.onSuccess(SuccessStatus.CREATED.getCode(), SuccessStatus.CREATED.getMessage(),"New Project created");
    }


    @GetMapping("/all-project")
    @ResponseBody
    public ApiResponse<AllProjectResponseDTO> projects(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        try {
            String username = customUserDetails.getUsername();
            List<ProjectDTO> allProjects = projectUserService.findAllProjects(username);

            AllProjectResponseDTO allProjectResponseDto = AllProjectResponseDTO.builder()
                    .projects(allProjects)
                    .build();

            return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), allProjectResponseDto);
        }
        catch (IllegalArgumentException e) {
            log.info("not logged in user");
            return ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED.getCode(),
                    ErrorStatus._UNAUTHORIZED.getMessage(), null);
        }
    }


    //프로젝트 보기
    @GetMapping("/project/{projectId}")
    public ApiResponse<ProjectResponseDTO> viewProject(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                @PathVariable("projectId") Long projectId) {

        Project project = projectService.findProjectById(projectId);
        String username = customUserDetails.getUsername();

        // private project 이므로 해당 유저가 project 에 속해있는지 확인
        if (!project.getIsPublic()) {
            //user 정보 가져오기
            try{
                User user = userService.findUserByUsername(username);

                // projectUser 없으면 Error return
                if (!projectUserService.checkProjectUserExists(project, user)) {
                    return ApiResponse.onFailure(ErrorStatus.PROJECT_USER_NOT_FOUND.getCode(),
                            ErrorStatus.PROJECT_USER_NOT_FOUND.getMessage(), null);
                }
            } catch (IllegalArgumentException e) {
                log.info("not logged in user");
                return ApiResponse.onFailure(ErrorStatus.PROJECT_NOT_PUBLIC.getCode(),
                        ErrorStatus.PROJECT_NOT_PUBLIC.getMessage(), null);
            }
        }

        ProjectResponseDTO projectResponseDTO = ProjectResponseDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .isPublic(project.getIsPublic())
                .projectImageURL(project.getProjectImageURL())
                .build();

        List<Node> nodes = project.getNodes();

        if(!nodes.isEmpty()){
            List<Node> parentNodes = nodeService.getParentNodes(nodes);
            parentNodes.forEach(node -> {
                NodeResponseDTO nodeResponseDTO = nodeService.getNodeResponseDTO(node, projectId);
                nodeResponseDTO.setProjectId(projectId);
                projectResponseDTO.addNodeResponseDTO(nodeResponseDTO);
            });
        }

        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), projectResponseDTO);
    }

    // 프로젝트 편집
    @PatchMapping("/update-project/{projectId}")
    public ApiResponse<String> editProject(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @PathVariable("projectId") Long projectId,
                                           @RequestBody UpdateProjectRequestDTO updateProjectRequestDTO) {
        String username = customUserDetails.getUsername();

        User findUser = userService.findUserByUsername(username);
        Project findProject = projectService.findProjectById(projectId);

        boolean checkProjectUser = projectUserService.checkProjectUserExists(findProject, findUser);
        // projectUser 없으면 Error return
        if(!checkProjectUser){
            return ApiResponse.onFailure(ErrorStatus.PROJECT_USER_NOT_FOUND.getCode(),
                    ErrorStatus.PROJECT_USER_NOT_FOUND.getMessage(), null);
        }

        if (updateProjectRequestDTO.getIsPublic() != null) {
            findProject.updateProjectIsPublic(updateProjectRequestDTO.getIsPublic());
        }
        if (updateProjectRequestDTO.getName() != null) {
            findProject.updateProjectName(updateProjectRequestDTO.getName());
        }
        if (updateProjectRequestDTO.getProjectImageURL() != null) {
            findProject.updateProjectImageURL(updateProjectRequestDTO.getProjectImageURL());
        }
        projectUserService.updateProjectUser(findProject, updateProjectRequestDTO);

        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(),"Project updated");
    }

}
