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
public class Project extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Boolean isPublic;

    private String projectImageURL;

    @OneToMany(mappedBy = "project")
    @Builder.Default
    private List<Node> nodes = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    @Builder.Default
    private List<ProjectUser> projectUsers = new ArrayList<>();

    public void updateNode(Node node){
        projectImageURL = node.getImageURL();
        nodes.add(node);
    }

    public void updateProjectImageURL(String projectImageURL){
        this.projectImageURL = projectImageURL;
    }

    public void updateProjectUsers(ProjectUser projectUser){
        projectUsers.add(projectUser);
    }
}
