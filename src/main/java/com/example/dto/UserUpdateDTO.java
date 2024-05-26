package com.example.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserUpdateDTO {
    private String name;
    private String email;
    private String profileImageURL;
    private String role;
}
