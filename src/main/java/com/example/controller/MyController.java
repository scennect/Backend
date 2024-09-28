package com.example.controller;

import com.example.dto.PrincipleDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyController {


    @GetMapping("/mypage")
    @ResponseBody
    public String mypage(@AuthenticationPrincipal PrincipleDetail principleDetail) {
        return principleDetail.getUsername();
    }
}
