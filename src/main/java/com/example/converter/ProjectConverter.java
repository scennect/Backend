package com.example.converter;

import com.example.domain.Project;
import com.example.dto.request.ProjectRequestDTO;
import com.example.dto.ProjectDTO;
import com.example.dto.response.AllProjectResponseDTO;
import com.example.dto.response.ProjectResponseDTO;

import java.util.List;


public class ProjectConverter {

        public static Project toProject(ProjectRequestDTO projectRequestDto){
            return Project.builder()
                    .name(projectRequestDto.getName())
                    .isPublic(projectRequestDto.getIsPublic())
                    .build();
        }

        public static ProjectDTO toProjectDTO(Project project){
            return ProjectDTO.builder()
                    .id(project.getId())
                    .name(project.getName())
                    .isPublic(project.getIsPublic())
                    .projectImageURL(project.getProjectImageURL())
                    .build();
        }

        public static AllProjectResponseDTO toAllProjectResponseDto(List<ProjectDTO> allProjects){
            return AllProjectResponseDTO.builder()
                    .projects(allProjects)
                    .build();
        }

        public static ProjectResponseDTO toProjectResponseDto(Project project){
            return ProjectResponseDTO.builder()
                    .id(project.getId())
                    .name(project.getName())
                    .isPublic(project.getIsPublic())
                    .projectImageURL(project.getProjectImageURL())
                    .build();
        }


}
