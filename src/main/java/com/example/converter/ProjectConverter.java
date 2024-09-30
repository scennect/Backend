package com.example.converter;

import com.example.domain.Project;
import com.example.dto.request.ProjectRequestDTO;
import com.example.dto.response.ProjectResponseDTO;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ProjectConverter {

    // ProjectRequestDTO 를 Project Entity로 변환
    public static Project toProjectEntity(ProjectRequestDTO projectRequestDTO) {
        return Project.builder()
                .name(projectRequestDTO.getName())
                .isPublic(projectRequestDTO.getIsPublic())
                .build();
    }

    // ProjectRequestDTO 를 Project Entity로 변환
    public static ProjectResponseDTO toProjectResponseDTO(Project project) {
        return ProjectResponseDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .isPublic(project.getIsPublic())
                .projectImageURL(project.getProjectImageURL())
                .build();
    }

}
