package com.example.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JoinDTO {
    private String username;
    private String password;

    private String name;
    private String email;
}
