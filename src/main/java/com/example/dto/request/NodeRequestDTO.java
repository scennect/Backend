package com.example.dto.request;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NodeRequestDTO {

    private String prompt;
    private int x;
    private int y;

    private Long parentNodeId;

    private Long projectId;

    private Boolean seed;
}
