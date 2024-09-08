package com.example.service;

import com.example.domain.Node;
import com.example.domain.Project;
import com.example.dto.request.ProjectRequestDTO;
import com.example.dto.ProjectDTO;
import com.example.dto.response.NodeResponseDTO;

import java.util.List;

public interface ProjectService {

    public void saveProject(ProjectRequestDTO projectRequestDto);

    public Project findProjectById(Long projectId);

    public void updateProject(Project project);
}
