package com.example.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDTO {

    private String username;
    private String name;
    private String role;
}
