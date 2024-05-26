package com.example.service;

import com.example.domain.Project;
import com.example.domain.User;
import com.example.dto.ProjectDTO;

import java.util.List;

public interface ProjectUserService {

    public void saveProjectUser(Project project, User user);


    public List<ProjectDTO> findAllProjects(String username);

    public boolean checkProjectUserExists(Project project, User user);
}
