package com.example.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
public class ProjectResponseDTO {

    private Long id;
    private String name;
    private Boolean isPublic;
    private String projectImageURL;

    @Builder.Default
    private List<NodeResponseDTO> nodes = new ArrayList<>();

    public void addNodeResponseDTO(NodeResponseDTO nodeResponseDTO) {
        nodes.add(nodeResponseDTO);
    }

}
