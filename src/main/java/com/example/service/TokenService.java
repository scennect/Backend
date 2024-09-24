package com.example.service;

import jakarta.servlet.http.Cookie;

public interface TokenService {

    public Cookie createCookie(String key, String value);
}
