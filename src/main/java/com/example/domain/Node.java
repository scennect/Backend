package com.example.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

    @OneToMany(mappedBy = "parentNode")
    @Builder.Default
    private List<Node> children = new ArrayList<>();

    public void updateUser(User user){
        this.user = user;
    }
    public void addChild(Node node){
        children.add(node);
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
