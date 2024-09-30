package com.example.dto.request;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NodeRequestDTO {

    private String prompt;

    private Long parentNodeId;

    private Long projectId;
}
