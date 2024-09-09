package com.example.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface TokenService {

    public Cookie createCookie(String key, String value);

    public void reissueRefreshToken(HttpServletRequest request, HttpServletResponse response);

}
