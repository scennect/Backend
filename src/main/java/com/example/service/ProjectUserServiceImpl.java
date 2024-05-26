package com.example.service;

import com.example.converter.ProjectConverter;
import com.example.domain.Project;
import com.example.domain.ProjectUser;
import com.example.domain.User;
import com.example.dto.ProjectDTO;
import com.example.repository.ProjectUserRepository;
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
public class ProjectUserServiceImpl implements ProjectUserService{

    private final ProjectUserRepository projectUserRepository;
    private final UserService userService;

    @Override
    public void saveProjectUser(Project project, User user) {

        ProjectUser projectUser = ProjectUser.builder()
                .project(project)
                .user(user)
                .build();
        project.updateProjectUsers(projectUser);
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
                    ProjectDTO projectDto = ProjectConverter.toProjectDTO(project);
                    return projectDto;
                })
                .collect(Collectors.toList());
        return response;
    }

    @Override
    public boolean checkProjectUserExists(Project project, User user) {
        return projectUserRepository.existsByProjectAndUser(project, user);

    }
}
