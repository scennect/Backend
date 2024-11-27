package com.example.converter;

import com.example.domain.Project;
import com.example.dto.request.ProjectRequestDTO;
import com.example.dto.response.ProjectResponseDTO;
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
    public static ProjectResponseDTO toProjectResponseDTO(Project project, String name) {
        return ProjectResponseDTO.builder()
                .id(project.getId())
                .projectName(project.getName())
                // 아래 name 은 사용자의 별명 이름
                .projectUserName(name)
                .isPublic(project.getIsPublic())
                .projectImageURL(project.getProjectImageURL())
                .build();
    }

}
