package com.example.dto.response;

import com.example.dto.ProjectDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class AllProjectResponseDTO {

    private List<ProjectDTO> projects;
}
