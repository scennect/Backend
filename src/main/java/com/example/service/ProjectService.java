package com.example.service;

import com.example.domain.Node;
import com.example.domain.Project;
import com.example.domain.User;
import com.example.dto.PrincipleDetail;
import com.example.dto.request.ProjectRequestDTO;
import com.example.dto.ProjectDTO;
import com.example.dto.request.UpdateProjectRequestDTO;
import com.example.dto.response.NodeResponseDTO;
import com.example.dto.response.ProjectResponseDTO;

import java.util.List;

public interface ProjectService {

    public Long saveProject(ProjectRequestDTO projectRequestDTO, User user);

    public ProjectResponseDTO viewProjectByIdAndUser(Long projectId, User user);

    public Project findProjectById(Long projectId);

    public void updateProject(Long projectId, User user, UpdateProjectRequestDTO updateProjectRequestDTO);

    public Project verifyProjectAccess(Long projectId, User user);

    public void addNode(Project project, Node node);
}
