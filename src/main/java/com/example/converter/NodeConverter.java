package com.example.converter;

import com.example.domain.Node;
import com.example.domain.Project;
import com.example.domain.User;
import com.example.dto.response.NodeResponseDTO;
import com.example.dto.response.ProjectResponseDTO;

public class NodeConverter {

    // Node Entity 로 변환
    public static Node toNodeEntity(String prompt, String imageURL, User user, Project project, Node parentNode) {
        return Node.builder()
                .prompt(prompt)
                .imageURL(imageURL)

                .user(user)
                .project(project)

                .parentNode(parentNode)

                .build();
    }
    // Node 를 NodeResponseDTO 로 변환
    public static NodeResponseDTO toNodeResponseDTO(Node node) {
        return NodeResponseDTO.builder()
                .id(node.getId())
                .name(node.getUser().getName())
                .prompt(node.getPrompt())
                .imageURL(node.getImageURL())

                .build();
    }
}
