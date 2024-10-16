package com.example.controller;

import com.example.apiPayload.ApiResponse;
import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.code.status.SuccessStatus;
import com.example.dto.JoinDTO;
import com.example.dto.LoginResponseDTO;
import com.example.dto.MouseDTO;
import com.example.dto.UserLoginDTO;
import com.example.service.TokenService;
import com.example.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/join")
    public ApiResponse<String> join(@RequestBody JoinDTO joinDTO) {
        userService.join(joinDTO);
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), "Join Success");
    }

    @PostMapping("/login2")
    public ApiResponse<String> login(@RequestBody UserLoginDTO userLoginDTO){
        try {
            // 서비스로 비즈니스 로직 위임
            LoginResponseDTO loginResponse = userService.authenticateAndGenerateTokens(userLoginDTO);

            return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), "Login Success");

        } catch (Exception e) {
            // 인증 실패 시 에러 응답 처리
            return ApiResponse.onFailure(ErrorStatus._BAD_REQUEST.getCode(), ErrorStatus._BAD_REQUEST.getMessage(), "Login Failure");
        }
    }

    @PostMapping("/reissue")
    public ApiResponse<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        tokenService.reissueRefreshToken(request, response);
        return ApiResponse.onSuccess(SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), "토큰 재발급 성공");
    }

    @MessageMapping("/mouse/{projectId}")
    @SendTo("/topic/mouse/{projectId}")
    public void moveMouse(@DestinationVariable String projectId, @Payload MouseDTO mouseDTO) {

        // 전달할 메시지 형식으로 변환
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setHeader("projectId", projectId);

        // name, x, y를 포함한 응답을 클라이언트에 전송
        messagingTemplate.convertAndSend("/topic/mouse/" + projectId, mouseDTO);
    }

//    @MessageMapping("/mouse/{projectId}")
//    @SendTo("/topic/mouse/{projectId}")
//    public MouseDTO sendMouseData(MouseDTO mouseDTO) {
//        return mouseDTO; // 수신된 마우스 데이터를 그대로 전송
//    }
}
