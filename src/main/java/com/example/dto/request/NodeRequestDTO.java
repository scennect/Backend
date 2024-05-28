package com.example.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class NodeRequestDTO {

    private String username;
    private String text;
    private Long parentNodeId;
    private Long projectId;
}
