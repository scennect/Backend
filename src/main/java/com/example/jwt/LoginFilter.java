package com.example.jwt;

import com.example.dto.LoginResponseDTO;
import com.example.dto.UserLoginDTO;
import com.example.redis.RedisClient;
import com.example.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.springframework.util.StreamUtils.copyToString;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final TokenService tokenService;
    private final RedisClient redisClient;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        //form으로 로그인 시, 클라이언트 요청에서 username과 password를 받아옴
//        String username = obtainUsername(request);
//        String password = obtainPassword(request);

        if (request.getContentType() == null || !request.getContentType().equals("application/json")) {
            throw new AuthenticationServiceException("Authentication Content-Type not supported: " + request.getContentType());
        }

        ObjectMapper objectMapper =  new ObjectMapper();

        ServletInputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String messageBody = null;
        try {
            messageBody = copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UserLoginDTO userLoginDTO = new UserLoginDTO();
        try {
            userLoginDTO = objectMapper.readValue(messageBody, UserLoginDTO.class);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        //클라이언트 요청에서 username, password 추출
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        //스프링 시큐리티에서 제공하는 token에 username과 password를 담음
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        //AuthenticationManager에게 token을 전달하면 인증을 진행하고 결과를 받아옴
        return authenticationManager.authenticate(authToken);
    }

    // 사용자 인증이 성공했을 때 실행
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        // user 정보 (아이디) 가져오기
        String username = authentication.getName();

        // GrantedAuthority 는 사용자의 권한
        // 사용자가 여러개의 권한 (roles, authorities)를 가질 수 있으므로 Collection 반환
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // 사용자의 권한들을 하나씩 순차적으로 접근하기 위해 Iterator 를 사용하여 authorities 컬렉션 순회 준비
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        // Iterator 를 이용하여 첫 번째 권한을 사용
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 토큰 생성
        String accessToken = jwtUtil.createJWT("access", username, role, 3600000L); // 10분
        String refreshToken = jwtUtil.createJWT("refresh", username, role, 86400000L); // 24시간

        // 응답 설정
        response.setHeader("access", accessToken);
        response.addCookie(tokenService.createCookie("refresh", refreshToken));
        response.setStatus(HttpStatus.OK.value());

        // redis 에 refresh 토큰 저장
        redisClient.setValue(username, refreshToken, 864000000L);


        LoginResponseDTO loginResponse = LoginResponseDTO.builder()
                .username(username)
                .role(role)
                .build();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), loginResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 실패 시 상태 코드 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("code", "MEMBER4001");
        errorResponse.put("message", "로그인 과정에서 오류가 발생했습니다.");

        try {
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(jsonResponse);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Failed to write authentication response body", e);
        }
    }

}