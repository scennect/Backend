package com.example.config;

import com.example.jwt.JWTFilter;
import com.example.jwt.JWTUtil;
import com.example.jwt.LoginFilter;
import com.example.redis.RedisClient;
import com.example.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity // Spring Security를 사용하기 위한 어노테이션
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final TokenService tokenService;
    private final RedisClient redisClient;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> {
            web.ignoring()
                    .requestMatchers("/join",
                            "/index.html", "/login.html", "/favicon.ico",
                            "/topic/**", "/app/**", "/ws/**",
                            "/api-docs/**", "/swagger-ui/**", "/swagger-ui.html/**", "/v3/api-docs/**", "/swagger-ui/index.html#/**",
                            "/css/**", "/js/**", "/img/**");// 필터를 타면 안되는 경로
        };
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http
                .cors(corsCustomizer -> corsCustomizer
                        .configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration corsConfiguration = new CorsConfiguration();

                        corsConfiguration.setAllowedOrigins(Collections.singletonList("http://52.78.73.212:8080"));

                        // GET, POST, PUT, DELETE, PATCH 등 모든 메소드 허용
                        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
                        corsConfiguration.setAllowCredentials(true);

                        //모든 헤더 허용
                        corsConfiguration.setExposedHeaders(Collections.singletonList("*"));
                        corsConfiguration.setMaxAge(3600L);

                        //백엔드에서 보낼때 헤더에 Authorization 추가
                        corsConfiguration.setExposedHeaders(Collections.singletonList("Authorization"));
                        return corsConfiguration;
                    }
                }));

        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/login", "/join", "/reissue",
                                "/index.html", "/login.html", "/favicon.ico",
                                "/topic/**", "/app/**", "/ws/**").permitAll()
                        .requestMatchers("/node/**", "/mypage", "/project/**").hasRole("USER")
                        .requestMatchers( "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        .anyRequest().authenticated());

        //JWTFilter 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http
                //UsernamePasswordAuthenticationFilter 자리에 LoginFilter를 추가
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, tokenService, redisClient), UsernamePasswordAuthenticationFilter.class);


        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }

}
