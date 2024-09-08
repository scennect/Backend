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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    private String name;

    private String email;

    private String profileImageURL;

    private String role;


    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<ProjectUser> myProjects = new ArrayList<>();

//    private String updateName(UserUpdateDto userUpdateDto){
//        this.name = userUpdateDto.getName();
//        return this.name;
//    }
//
//    private String updateEmail(UserUpdateDto userUpdateDto){
//        this.email = userUpdateDto.getEmail();
//        return this.email;
//    }
//
//    private String updateProfileImage(UserUpdateDto userUpdateDto){
//        this.profile_image = userUpdateDto.getProfileImageURL();
//        return this.profile_image;
//    }
//
//    private String updateRole(UserUpdateDto userUpdateDto){
//        this.role = userUpdateDto.getRole();
//        return this.role;
//    }
}
