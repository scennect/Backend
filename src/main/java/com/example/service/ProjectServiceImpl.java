package com.example.service;

import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.exception.GeneralException;
import com.example.converter.ProjectConverter;
import com.example.domain.Node;
import com.example.domain.Project;
import com.example.domain.User;
import com.example.dto.request.ProjectRequestDTO;
import com.example.dto.request.UpdateProjectRequestDTO;
import com.example.dto.response.NodeResponseDTO;
import com.example.dto.response.ProjectResponseDTO;
import com.example.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;

    private final NodeService nodeService;
    private final ProjectUserService projectUserService;

    // 프로젝트 처음 생성할때 실행
    @Override
    public void saveProject(ProjectRequestDTO projectRequestDTO, User user) {

        // 프로젝트 저장 : 프로젝트명, 공개여부로 project build
        Project project = ProjectConverter.toProjectEntity(projectRequestDTO);

        // 프로젝트 생성 유저 저장
        projectUserService.saveProjectUser(project, user);

        // 팀원 초대 처리
        Optional.ofNullable(projectRequestDTO.getMemberEmails())
                .ifPresent(emailList -> emailList.forEach(email ->
                        projectUserService.saveProjectUserByEmail(project, email)));

        projectRepository.save(project);
    }


    @Override
    public ProjectResponseDTO viewProjectByIdAndUser(Long projectId, User user) {
        Project project = findProjectById(projectId);

        // private project 이므로 해당 유저가 project 에 속해있는지 확인
        if (!project.getIsPublic()) {
            // projectUser 없으면 Error return
            if (!projectUserService.checkProjectUserExists(project, user)) {
                throw new GeneralException(ErrorStatus.PROJECT_USER_NOT_FOUND);
            }
        }

        ProjectResponseDTO projectResponseDTO = ProjectConverter.toProjectResponseDTO(project);

        List<Node> nodes = project.getNodes();

        if(!nodes.isEmpty()){
            // 부모 노드들 가져오기
            List<Node> parentNodes = nodeService.getParentNodes(nodes);
            // 각 부모 노드에 대해 자식 노드들 포함한  호출
            parentNodes.forEach(node -> {
                NodeResponseDTO nodeResponseDTO = nodeService.getNodeResponseDTO(node);
                projectResponseDTO.addNodeResponseDTO(nodeResponseDTO);
            });
        }

        return projectResponseDTO;
    }

    @Override
    public Project findProjectById(Long projectId) {

        return projectRepository.findById(projectId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PROJECT_NOT_FOUND));
    }

    // 프로젝트 이름, 공개여부, 팀원 변경시 사용
    @Override
    public void updateProject(Long projectId, User user, UpdateProjectRequestDTO updateProjectRequestDTO) {

        Project project = findProjectById(projectId);

        // 프로젝트 권한이 없으므로 Error return
        if (!projectUserService.checkProjectUserExists(project, user)) {
            throw new GeneralException(ErrorStatus.PROJECT_USER_NOT_FOUND);
        }

        if (updateProjectRequestDTO.getIsPublic() != null) {
            project.updateProjectIsPublic(updateProjectRequestDTO.getIsPublic());
        }
        if (updateProjectRequestDTO.getName() != null) {
            project.updateProjectName(updateProjectRequestDTO.getName());
        }
        if (updateProjectRequestDTO.getProjectImageURL() != null) {
            project.updateProjectImageURL(updateProjectRequestDTO.getProjectImageURL());
        }

        // 팀원 초대 처리
        Optional.ofNullable(updateProjectRequestDTO.getMemberEmails())
                .ifPresent(emailList -> emailList.forEach(email ->
                        projectUserService.saveProjectUserByEmail(project, email)));

        projectRepository.save(project);
    }

    @Override
    public Project verifyProjectAccess(Long projectId, User user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PROJECT_NOT_FOUND));

        if (!projectUserService.checkProjectUserExists(project, user)) {
            throw new GeneralException(ErrorStatus.PROJECT_USER_NOT_FOUND);
        }
        return project;
    }

    @Override
    public void addNode(Project project, Node node) {
        project.updateNode(node);
        projectRepository.save(project);
    }

}
