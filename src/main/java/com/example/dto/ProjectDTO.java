package com.example.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProjectDTO {
    private Long id;
    private String name;
    private String projectImageURL;
    private Boolean isPublic;
}
