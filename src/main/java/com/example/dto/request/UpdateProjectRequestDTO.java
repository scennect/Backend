package com.example.dto.request;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProjectRequestDTO {

        private String name;
        private Boolean isPublic;
        private String projectImageURL;
        private List<String> memberEmails;
}
