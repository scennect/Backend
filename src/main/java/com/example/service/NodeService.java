package com.example.service;

import com.example.domain.Node;
import com.example.domain.Project;
import com.example.domain.User;
import com.example.dto.CoordinateDTO;
import com.example.dto.request.NodeRequestDTO;
import com.example.dto.response.NodeResponseDTO;

import java.util.List;

public interface NodeService {

    public NodeResponseDTO saveNode(NodeRequestDTO nodeRequestDto, User user, Project project);

    public void DeleteNodeByIdAndUser(Long nodeId, User user);

    public NodeResponseDTO getNodeResponseDTO(Node firstNode);

    public List<Node> getParentNodes(List<Node> nodes);

    public Node findNodeById(Long nodeId);

    public void updateCoordinate(Long nodeId, User user, CoordinateDTO coordinateDTO);

}
