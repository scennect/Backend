package com.example.jwt;

import com.example.apiPayload.code.status.ErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JWTException {

    public static void jwtExceptionHandler(HttpServletResponse response, ErrorStatus errorStatus) {

        response.setStatus(errorStatus.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", errorStatus.getCode());
        errorResponse.put("message", errorStatus.getMessage());

        try {
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(jsonResponse);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Failed to write authentication response body", e);
        }
    }
}

