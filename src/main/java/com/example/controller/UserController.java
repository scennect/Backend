package com.example.controller;

import com.example.apiPayload.ApiResponse;
import com.example.apiPayload.code.status.SuccessStatus;
import com.example.dto.JoinDTO;
import com.example.service.TokenService;
import com.example.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/join")
    public ApiResponse<String> join(@RequestBody JoinDTO joinDTO) {
        userService.join(joinDTO);
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), "Join Success");
    }

    @PostMapping("/reissue")
    public ApiResponse<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        tokenService.reissueRefreshToken(request, response);
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), "토큰 재발급 성공");
    }
}
