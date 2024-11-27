package com.example.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
public class NodeResponseDTO {
    private Long id;
    private String name;
    private String prompt;
    private String imageURL;
    private Long parentNodeId;

    private int x;
    private int y;

    @Builder.Default
    private List<NodeResponseDTO> children = new ArrayList<>();

    public void addChildResponseDTO(NodeResponseDTO childNodeResponseDTO) {
        children.add(childNodeResponseDTO);
    }
}
