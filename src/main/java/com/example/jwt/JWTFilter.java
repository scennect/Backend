package com.example.jwt;


import com.example.domain.User;
import com.example.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //request에서 헤더에 있는 토큰을 받아옴
        String authorization = request.getHeader("Authorization");

        // 토큰이 없거나 Bearer로 시작하지 않으면 필터체인을 통과시킴
        if(authorization == null || !authorization.startsWith("Bearer ")) {
            System.out.println("token = " + authorization);
            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료
            return;
        }

        //Bearer로 시작하는 토큰이 있으면 뒷부분만 떼어냄
        String token = authorization.split(" ")[1];

        //토큰 소멸시간 검증 -> 만료됐으면 다음 필터 진행
        if(jwtUtil.isExpired(token)) {
            System.out.println("토큰 만료");
            filterChain.doFilter(request, response);

            return;
        }

        String username = jwtUtil.getUserNameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);

        User user = User.builder()
                .username(username)
                .role(role)
                .password("tempPassword")
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
