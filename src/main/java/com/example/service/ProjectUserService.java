package com.example.service;

import com.example.domain.Project;
import com.example.domain.User;
import com.example.dto.ProjectDTO;
import com.example.dto.request.UpdateProjectRequestDTO;

import java.util.List;

public interface ProjectUserService {

    public void saveProjectUser(Project project, User user);

    public void saveProjectUserByEmail(Project project, String email);

    public List<ProjectDTO> findAllProjects(User user);

    public boolean checkProjectUserExists(Project project, User user);

    public void updateProjectUser(Project project, UpdateProjectRequestDTO updateProjectRequestDTO);

}
