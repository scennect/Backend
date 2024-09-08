package com.example.service;

import com.example.domain.Project;
import com.example.domain.ProjectUser;
import com.example.domain.User;
import com.example.dto.ProjectDTO;
import com.example.dto.request.UpdateProjectRequestDTO;
import com.example.repository.ProjectUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProjectUserServiceImpl implements ProjectUserService{

    private final ProjectUserRepository projectUserRepository;
    private final UserService userService;

    @Override
    public void saveProjectUser(Project project, User user) {

        ProjectUser projectUser = ProjectUser.builder()
                .project(project)
                .user(user)
                .build();
        // 프로젝트에 유저 추가
        project.updateProjectUsers(projectUser);
        // 프로젝트 유저 저장
        projectUserRepository.save(projectUser);
    }


    @Override
    // 내가 속한 프로젝트들 조회
    public List<ProjectDTO> findAllProjects(String username) {
        User user = userService.findUserByUsername(username);

        List<ProjectUser> myProjects = user.getMyProjects();

        List<ProjectDTO> response = myProjects.stream()
                .map(projectUser -> {
                    Project project = projectUser.getProject();

                    ProjectDTO projectDto = ProjectDTO.builder()
                            .id(project.getId())
                            .name(project.getName())
                            .isPublic(project.getIsPublic())
                            .projectImageURL(project.getProjectImageURL())
                            .build();

                    return projectDto;
                })
                .collect(Collectors.toList());
        return response;
    }

    @Override
    public boolean checkProjectUserExists(Project project, User user) {
        // 프로젝트에 유저가 속해있으면 true, 아니면 false
        return projectUserRepository.existsByProjectAndUser(project, user);

    }

    @Override
    public void updateProjectUser(Project project, UpdateProjectRequestDTO updateProjectRequestDTO) {

        List<String> memberEmails = updateProjectRequestDTO.getMemberEmails();
        if(memberEmails == null || memberEmails.isEmpty()) {
            return;
        }
        // 팀원 초대 있을 시, 팀원들에 대한 ProjectUser 저장
        memberEmails.stream().forEach(memberEmail -> {
            User member = userService.findUserByEmail(memberEmail);
            if(!checkProjectUserExists(project, member)) {
                saveProjectUser(project, member);
            }
        });
    }
}
