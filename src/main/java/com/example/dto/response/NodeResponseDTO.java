package com.example.dto.response;

import com.example.domain.Node;
import com.example.domain.Project;
import com.example.domain.User;
import jakarta.persistence.*;
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
    private String text;
    private String imageURL;
    private String username;
    private Long parentNodeId;
    private Long projectId;

    @Builder.Default
    private List<NodeResponseDTO> children = new ArrayList<>();

    public void addChildResponseDTO(NodeResponseDTO childNodeResponseDTO) {
        children.add(childNodeResponseDTO);
    }
}
