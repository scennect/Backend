package com.example.service;

import com.example.domain.Node;
import com.example.dto.request.NodeRequestDTO;
import com.example.dto.response.NodeResponseDTO;
import com.example.websocket.NodePositionDTO;

import java.util.List;

public interface NodeService {

    public Long saveNode(NodeRequestDTO nodeRequestDto);

    public NodeResponseDTO getNodeResponseDTO(Node firstNode);

    public List<Node> getParentNodes(List<Node> nodes);

    public Node findById(Long nodeId);

    public void updateNodePosition(NodePositionDTO nodePositionDTO);

    public List<Node> getNodesByProjectId(Long projectId);

    public void updateNodeImageURL(Long nodeId, String imageURL);
}
