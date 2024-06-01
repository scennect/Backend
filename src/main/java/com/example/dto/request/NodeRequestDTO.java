package com.example.dto.request;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NodeRequestDTO {

    private String username;
    private String text;
    private String imageURL;
    private Long parentNodeId;
    private Long projectId;
}
