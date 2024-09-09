package com.example.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Node extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private String imageURL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Node parentNode;

    // 부모가 삭제될 때 자식 엔티티는 삭제되지 않음 (CascadeType.REMOVE 사용 안 함)
    @OneToMany(mappedBy = "parentNode", cascade = CascadeType.PERSIST, orphanRemoval = false)
    @Builder.Default
    private LinkedList<Node> children = new LinkedList<>();

    public void updateUser(User user){
        this.user = user;
    }

    public void addChild(Node node){
        children.addLast(node);
    }

    public void updateParentNode(Node parentNode){
        this.parentNode = parentNode;
    }

    public void updateProject(Project project){
        this.project = project;
    }

    public void updateImageURL(String imageURL){
        this.imageURL = imageURL;
    }
}
