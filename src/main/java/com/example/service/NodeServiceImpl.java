package com.example.service;

import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.exception.GeneralException;
import com.example.converter.NodeConverter;
import com.example.domain.Node;
import com.example.domain.Project;
import com.example.domain.User;
import com.example.dto.request.NodeRequestDTO;
import com.example.dto.response.NodeResponseDTO;
import com.example.repository.NodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NodeServiceImpl implements NodeService{

    private final NodeRepository nodeRepository;

    private final ImageService imageService;

    @Override
    public Long saveNode(NodeRequestDTO nodeRequestDto, User user, Project project) {

        // Prompt가 제대로 들어왔는지 확인
        if(nodeRequestDto.getPrompt() == null || nodeRequestDto.getPrompt().isEmpty()){
            throw new GeneralException(ErrorStatus.NODE_INVALID_PROMPT);
        }

        String imageURL;
        Node parentNode = null;

        // 부모 노드 확인
        Long parentNodeId = nodeRequestDto.getParentNodeId();
        if (parentNodeId != null) {
            parentNode = findNodeById(nodeRequestDto.getParentNodeId());
            imageURL = imageService.generateImageToImage(nodeRequestDto.getPrompt(), parentNode.getImageURL());

        }
        else {
            imageURL = imageService.generateTextToImage(nodeRequestDto.getPrompt());

            // 로컬에서 위에 generateImage 없이 돌릴때 사용할 용도
            //imageURL = "s3_url :\"https://hongik-s3.s3.amazonaws.com/42_10_7.5.png\"";

        }

        // build new node
        Node newNode = NodeConverter.toNodeEntity(nodeRequestDto.getPrompt(), imageURL, user, project, parentNode);

        if (parentNode != null) {
            parentNode.addChild(newNode);
        }

        // save new node
        Node saveNode = nodeRepository.save(newNode);
        return saveNode.getId();
    }

    @Override
    public void DeleteNodeByIdAndUser(Long nodeId, User user) {
        // nodeId 로 node 찾기
        Node findNode = findNodeById(nodeId);

        // node 생성자 인지 확인
        if (!findNode.getUser().equals(user)) {
            throw new GeneralException(ErrorStatus.NODE_INVALID_USER);
        }
        else {
            Node parentNode = findNode.getParentNode();
            // 부모노드가 있는 경우
            if (parentNode!=null) {
                List<Node> children = parentNode.getChildren();
                // 부모노드의 자식 중 findNode를 삭제
                children.removeIf(child -> child.equals(findNode));
            }

            // 자식 노드들에 대해 부모 노드(삭제 요청한 노드) 삭제
            List<Node> children = findNode.getChildren();
            for (Node child : children) {
                child.updateParentNode(null);
            }

            Project project = findNode.getProject();

            // 프로젝트의 노드 중 삭제하려는 노드를 삭제
            List<Node> nodes = project.getNodes();
            nodes.removeIf(node -> node.equals(findNode));

            // 노드 삭제
            nodeRepository.delete(findNode);
        }

    }

    @Override
    public NodeResponseDTO getNodeResponseDTO(Node node) {
        NodeResponseDTO nodeResponseDTO = NodeConverter.toNodeResponseDTO(node);

        // 자식 노드가 존재하는 경우
        if(!node.getChildren().isEmpty()){
            node.getChildren().forEach(childNode -> {
                NodeResponseDTO childNodeResponseDTO = getNodeResponseDTO(childNode);
                childNodeResponseDTO.setParentNodeId(node.getId());
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

        return nodes.stream()
                .filter(node -> node.getParentNode() == null)
                .collect(Collectors.toList());
    }



    @Override
    public Node findNodeById(Long nodeId) {
        return nodeRepository.findById(nodeId).orElseThrow(()
                -> new GeneralException(ErrorStatus.NODE_NOT_FOUND));
    }

}
