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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.apiPayload.code.status.ErrorStatus.NODE_NOT_CORRECT;
import static com.example.apiPayload.code.status.ErrorStatus.PROJECT_USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class NodeServiceImpl implements NodeService{

    private final NodeRepository nodeRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectUserServiceImpl projectUserServiceImpl;

    @Override
    public Long saveNode(NodeRequestDTO nodeRequestDto) {

        // check if node is correct
        if(nodeRequestDto.getText() == null || nodeRequestDto.getImageURL()==null){
            throw new GeneralException(NODE_NOT_CORRECT);
        }

        // build new node
        Node newNode = Node.builder()
                .text(nodeRequestDto.getText())
                .imageURL(nodeRequestDto.getImageURL())
                .build();

        // check if user exists correct
        if(nodeRequestDto.getUsername() != null){
            User checkUser = userRepository.findByUsername(nodeRequestDto.getUsername()).orElseThrow(
                    () -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
            newNode.updateUser(checkUser);

            // check project and update
            if(nodeRequestDto.getProjectId() != null){
                Long projectId = nodeRequestDto.getProjectId();

                Project checkProject = projectRepository.findById(projectId)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.PROJECT_NOT_FOUND));

                // check ProjectUserExists
                boolean checkProjectUserExists = projectUserServiceImpl.checkProjectUserExists(checkProject, checkUser);
                if(checkProjectUserExists){
                    // 노드에 프로젝트 업데이트
                    newNode.updateProject(checkProject);

                    // 프로젝트에 노드 업데이트
                    checkProject.updateNode(newNode);
                    projectRepository.save(checkProject);
                }
            }
        }
        // not login user
        else{
            // 로그인하지 않았는데 프로젝트에 저장하려고 한 경우
            if (nodeRequestDto.getProjectId() != null) {
                new GeneralException(ErrorStatus.PROJECT_USER_NOT_FOUND);
            }
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
        Node saveNode = nodeRepository.save(newNode);
        return saveNode.getId();
    }

    @Override
    public void DeleteNodeByIdAndUser(Long nodeId, User user) {
        // nodeId 로 node 찾기
        Node findNode = nodeRepository.findById(nodeId).orElseThrow(()
                -> new GeneralException(ErrorStatus.NODE_NOT_FOUND));

        // node 생성자 인지 확인
        if (!findNode.getUser().equals(user)) {
            new GeneralException(ErrorStatus.NODE_INVALID_USER);
        }
        else {
            Node parentNode = findNode.getParentNode();
            // 부모노드가 없는 경우
            if (parentNode == null) {

            }
            // 부모노드가 있는 경우
            else {
                List<Node> children = parentNode.getChildren();
                // 부모노드의 자식 중 findNode를 삭제
                children.removeIf(child -> child.equals(findNode));

            }

            // 자식 노드들에 대해 부모 노드 삭제
            List<Node> children = findNode.getChildren();
            for (Node child : children) {
                child.updateParentNode(null);
            }

            // 만약 노드가 프로젝트에 속해 있는 경우
            Project project = findNode.getProject();
            if (project != null) {
                // 프로젝트의 노드 중 삭제하려는 노드를 삭제
                List<Node> nodes = project.getNodes();
                nodes.removeIf(node -> node.equals(findNode));
            }

            // 노드 삭제
            nodeRepository.delete(findNode);
        }

    }

    @Override
    public NodeResponseDTO getNodeResponseDTO(Node node, Long projectId) {
        NodeResponseDTO nodeResponseDTO = NodeResponseDTO.builder()
                .id(node.getId())
                .text(node.getText())
                .imageURL(node.getImageURL())
                .build();

        if(!node.getChildren().isEmpty()){
            node.getChildren().forEach(childNode -> {
                NodeResponseDTO childNodeResponseDTO = getNodeResponseDTO(childNode, projectId);
                childNodeResponseDTO.setParentNodeId(node.getId());
                childNodeResponseDTO.setProjectId(projectId);
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


    @Override
    public List<NodeRequestDTO> getNodesByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(()
                -> new GeneralException(ErrorStatus.PROJECT_NOT_FOUND));
        List<Node> nodes = project.getNodes();
        List<NodeRequestDTO> collect = nodes.stream().map(node -> {
            return NodeRequestDTO.builder()
                    .text(node.getText())
                    .imageURL(node.getImageURL())
                    .build();
        }).collect(Collectors.toList());

        return collect;
    }

    @Override
    public void checkParentNode(NodeRequestDTO nodeRequestDto) {
        Long parentNodeId = nodeRequestDto.getParentNodeId();
        Node parentNode = nodeRepository.findById(parentNodeId).orElseThrow(()
                -> new GeneralException(ErrorStatus.NODE_NOT_FOUND));

        if (!nodeRequestDto.getParentImageURL().equals(parentNode.getImageURL())){
            throw new GeneralException(ErrorStatus.PARENT_IMAGE_URL_NOT_CORRECT);
        }
    }

}
