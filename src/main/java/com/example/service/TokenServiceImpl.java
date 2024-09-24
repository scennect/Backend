package com.example.service;

import com.example.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService{

    private final JWTUtil jwtUtil;

    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60); // 24시간
        //cookie.setSecure(true); //https를 사용할 경우
        cookie.setPath("/"); // 쿠키가 적용될 경로
        cookie.setHttpOnly(true); // javascript 접근 못하게 하기 위함

        return cookie;
    }


}
