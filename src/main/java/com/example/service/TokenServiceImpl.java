package com.example.service;

import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.exception.GeneralException;
import com.example.jwt.JWTUtil;
import com.example.redis.RedisClient;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService{

    private final JWTUtil jwtUtil;
    private final RedisClient redisClient;

    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60); // 24시간
        //cookie.setSecure(true); //https를 사용할 경우
        cookie.setPath("/"); // 쿠키가 적용될 경로
        cookie.setHttpOnly(true); // javascript 접근 못하게 하기 위함

        return cookie;
    }

    @Override
    public void reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) {

        // refresh token 추출
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refreshToken = cookie.getValue();
            }
        }

        // refresh 토큰이 없는 경우
        if (refreshToken.isEmpty()) {
            // response status code
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_NOT_FOUND);

        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategoryFromToken(refreshToken);

        // 토큰의 카테고리가 refresh 가 아닌 경우
        if (!category.equals("refresh")) {
            //response status code
            throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        String username = jwtUtil.getUserNameFromToken(refreshToken);
        String role = jwtUtil.getRoleFromToken(refreshToken);

        //DB에 저장되어 있는지 확인
        String redisRefresh = redisClient.getValue(username);
        if (StringUtils.isEmpty(redisRefresh) || !refreshToken.equals(redisRefresh)) {

            //response body
            throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        // 새 JWT 발급
        String newAccessToken = jwtUtil.createJWT("access", username, role, 600000L);
        String newRefreshToken = jwtUtil.createJWT("refresh", username, role, 86400000L);

        // 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        redisClient.deleteValue(username);
        redisClient.setValue(username, newRefreshToken, 864000000L);

        //response
        response.setHeader("access", newAccessToken);
        response.addCookie(createCookie("refresh", newRefreshToken));
        response.setStatus(HttpStatus.OK.value());
    }

}
