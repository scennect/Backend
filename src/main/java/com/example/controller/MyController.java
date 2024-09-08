package com.example.controller;

import com.example.dto.CustomUserDetails;
import com.example.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyController {

    private final JWTUtil jwtUtil;

    @GetMapping("/mypage")
    @ResponseBody
    public String mypage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return customUserDetails.getUsername();
    }
}
