package com.example.service;

import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.exception.GeneralException;
import com.example.converter.ProjectConverter;
import com.example.domain.Node;
import com.example.domain.Project;
import com.example.domain.User;
import com.example.dto.request.ProjectRequestDTO;
import com.example.repository.NodeRepository;
import com.example.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;
    private final NodeRepository nodeRepository;
    private final UserService userService;
    private final ProjectUserService projectUserService;

    @Override
    public void saveProject(ProjectRequestDTO projectRequestDto) {
        //check if user exists
        User user = userService.findUserByUsername(projectRequestDto.getUsername());

        // 프로젝트명, 공개여부로 project build
        Project project = ProjectConverter.toProject(projectRequestDto);

        // 프로젝트 생성 유저 저장
        projectUserService.saveProjectUser(project, user);

        // 팀원 초대 있을 시, 팀원들에 대한 ProjectUser 저장
        List<String> memberEmails = projectRequestDto.getMemberEmails();
        if(memberEmails != null && !memberEmails.isEmpty()) {
            memberEmails.stream().forEach(memberEmail -> {
                try {
                    User member = userService.checkIfUserExistsByEmail(memberEmail);
                    projectUserService.saveProjectUser(project, member);
                } catch (GeneralException e) {
                    log.info("User not found with email: " + memberEmail);
                }
            });
        }

        // 저장할 노드가 있는 프로젝트
        List<Long> nodeIds = projectRequestDto.getNodeIds();
        if (!nodeIds.isEmpty()) {
            List<Node> nodesList = nodeIds.stream()
                    .map(nodeId -> nodeRepository.findById(nodeId)
                            .orElseThrow(() -> new GeneralException(ErrorStatus.NODE_NOT_FOUND)))
                    .collect(Collectors.toList());

            // 프로젝트 image url 설정 (첫번째 노드의 image url)
            String projectImageURL = nodesList.get(0).getImageURL();
            project.updateProjectImageURL(projectImageURL);

            // 노드들의 프로젝트 정보 업데이트
            nodesList.stream().forEach(node -> node.updateProject(project));
            nodesList.stream().forEach(node -> nodeRepository.save(node));

            nodesList.stream().forEach(node -> project.updateNode(node));
        }

        projectRepository.save(project);
    }

    @Override
    public Project findProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PROJECT_NOT_FOUND));
        return project;
    }


}
