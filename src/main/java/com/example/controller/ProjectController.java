package com.example.controller;

import com.example.apiPayload.ApiResponse;
import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.code.status.SuccessStatus;
import com.example.converter.ProjectConverter;
import com.example.domain.Node;
import com.example.domain.Project;
import com.example.domain.User;
import com.example.dto.ProjectDTO;
import com.example.dto.request.ProjectRequestDTO;
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
    public ApiResponse<String> newProject(@CookieValue String Authorization,
                                          @RequestBody ProjectRequestDTO projectRequestDTO) {
        String username = jwtUtil.getUsername(Authorization);
        projectRequestDTO.setUsername(username);

        projectService.saveProject(projectRequestDTO);

        return ApiResponse.onSuccess(SuccessStatus.CREATED.getCode(), SuccessStatus.CREATED.getMessage(),"New Project created");
    }


    @GetMapping("/all-project")
    @ResponseBody
    public ApiResponse<AllProjectResponseDTO> projects(@CookieValue(value = "Authorization", required = false) String Authorization) {

        try {
            String username = jwtUtil.getUsername(Authorization);
            List<ProjectDTO> allProjects = projectUserService.findAllProjects(username);

            AllProjectResponseDTO allProjectResponseDto = ProjectConverter.toAllProjectResponseDto(allProjects);
            return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), allProjectResponseDto);
        }
        catch (IllegalArgumentException e) {
            log.info("not logged in user");
            return ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED.getCode(),
                    ErrorStatus._UNAUTHORIZED.getMessage(), null);
        }
    }


    @GetMapping("/project/{projectId}")
    public ApiResponse<ProjectResponseDTO> node(@CookieValue(value = "Authorization", required = false) String Authorization,
                                                @PathVariable("projectId") Long projectId) {

        Project project = projectService.findProjectById(projectId);
        String username = jwtUtil.getUsername(Authorization);

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

        ProjectResponseDTO projectResponseDTO = ProjectConverter.toProjectResponseDto(project);

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


}
