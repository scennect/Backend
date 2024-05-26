package com.example.service;

import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.exception.GeneralException;
import com.example.domain.Node;
import com.example.domain.Project;
import com.example.domain.User;
import com.example.dto.request.NodeRequestDTO;
import com.example.dto.response.NodeResponseDTO;
import com.example.repository.NodeRepository;
import com.example.repository.ProjectRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.apiPayload.code.status.ErrorStatus.NODE_NOT_CORRECT;

@Service
@RequiredArgsConstructor
@Transactional
public class NodeServiceImpl implements NodeService{

    private final NodeRepository nodeRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public void saveNode(NodeRequestDTO nodeRequestDto) {

        // check if node is correct
        if(nodeRequestDto.getText() == null || nodeRequestDto.getImageURL() == null){
            throw new GeneralException(NODE_NOT_CORRECT);
        }

        // build new node
        Node newNode = Node.builder()
                .text(nodeRequestDto.getText())
                .imageURL(nodeRequestDto.getImageURL())
                .build();

        // check if user is correct
        if(nodeRequestDto.getUsername() != null){
            User checkUser = userRepository.findByUsername(nodeRequestDto.getUsername()).orElseThrow(
                    () -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
            newNode.updateUser(checkUser);
        }

        // check project and update
        if(nodeRequestDto.getProjectId() != null){
            Long projectId = nodeRequestDto.getProjectId();

            Project checkProject = projectRepository.findById(projectId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.PROJECT_NOT_FOUND));

            newNode.updateProject(checkProject);
            checkProject.updateNode(newNode);
            projectRepository.save(checkProject);
        }

        // 부모노드 update
        if (nodeRequestDto.getParentNodeId() != null) {
            Node parentNode = nodeRepository.findById(nodeRequestDto.getParentNodeId()).orElseThrow(
                    () -> new GeneralException(ErrorStatus.NODE_NOT_FOUND));

            newNode.updateParentNode(parentNode);
            parentNode.addChild(newNode);
            nodeRepository.save(parentNode);
        }

        // save new node
        nodeRepository.save(newNode);
    }

    @Override
    public NodeResponseDTO getNodeResponseDTO(Node parentNode) {
        NodeResponseDTO nodeResponseDTO = NodeResponseDTO.builder()
                .id(parentNode.getId())
                .text(parentNode.getText())
                .imageURL(parentNode.getImageURL())
                .build();

        if(!parentNode.getChildren().isEmpty()){
            parentNode.getChildren().stream().forEach(childNode -> {
                NodeResponseDTO childNodeResponseDTO = getNodeResponseDTO(childNode);
                nodeResponseDTO.addChildResponseDTO(childNodeResponseDTO);
            });
        }
        else {
            nodeResponseDTO.setChildren(null);
        }

        return nodeResponseDTO;
    }

    @Override
    public List<Node> getParentNodes(List<Node> nodes){
        List<Node> parentNodes = nodes.stream()
                .filter(node -> node.getParentNode() == null)
                .collect(Collectors.toList());

        return parentNodes;
    }

}
