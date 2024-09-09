package com.example.jwt;


import com.example.domain.User;
import com.example.dto.PrincipleDetail;
import com.example.dto.UserDTO;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 access key에 담긴 토큰을 추출
        String accessToken = request.getHeader("access");

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {
            System.out.println("access token is null");
            filterChain.doFilter(request, response);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰 만료 여부 확인
        try{
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            // 만료 시 다음 필터로 넘기지 않는다.
            // response body
            PrintWriter writer = response.getWriter();
            writer.println("access token expired");

            // response status cod
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 만료되지 않은 토큰의 category가 access 인지 확인 (발급 시 페이로드에 명시)
        String category = jwtUtil.getCategoryFromToken(accessToken);

        if (!category.equals("access")) {

            // response body
            PrintWriter writer = response.getWriter();
            writer.println("invalid access token");

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰에서 username, role 값을 추출
        String username = jwtUtil.getUserNameFromToken(accessToken);
        String role = jwtUtil.getRoleFromToken(accessToken);

        UserDTO userDTO = UserDTO.builder()
                .username(username)
                .role(role)
                .build();

        // UserDeatil 에 회원 정보 객체 담기
        PrincipleDetail principleDetail = new PrincipleDetail(userDTO);

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(principleDetail, null, principleDetail.getAuthorities());

        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

}
