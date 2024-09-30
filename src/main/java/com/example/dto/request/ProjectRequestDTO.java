package com.example.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
public class ProjectRequestDTO {

    private String name;
    private Boolean isPublic;
    private List<String> memberEmails;

}
