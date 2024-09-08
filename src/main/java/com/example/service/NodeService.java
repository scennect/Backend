package com.example.service;

import com.example.domain.Node;
import com.example.dto.request.NodeRequestDTO;
import com.example.dto.response.NodeResponseDTO;

import java.util.List;

public interface NodeService {

    public Long saveNode(NodeRequestDTO nodeRequestDto);

    public NodeResponseDTO getNodeResponseDTO(Node firstNode, Long projectId);

    public List<Node> getParentNodes(List<Node> nodes);

    public List<NodeRequestDTO> getNodesByProjectId(Long projectId);

    public void checkParentNode(NodeRequestDTO nodeRequestDto);

}
