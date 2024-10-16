package com.example.service;

import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.exception.GeneralException;
import com.example.domain.Node;
import com.example.domain.User;
import com.example.dto.JoinDTO;
import com.example.dto.LoginResponseDTO;
import com.example.dto.PrincipleDetail;
import com.example.dto.UserLoginDTO;
import com.example.jwt.JWTUtil;
import com.example.redis.RedisClient;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Iterator;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    private final RedisClient redisClient;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    @Override
    public User checkIfUserExistsByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND_BY_EMAIL));
    }

    @Override
    public User loadMemberByPrincipleDetail(PrincipleDetail principalDetail) {
        // 현재 로그인한 사용자 정보 가져오기
        String username = principalDetail.getUsername();

        return userRepository.findByUsername(username).orElseThrow(()
                -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    @Override
    public void join(JoinDTO joinDTO){

        if(userRepository.existsByUsername(joinDTO.getUsername())) {
            throw new GeneralException(ErrorStatus.USER_ALREADY_EXISTS);
        }

        User member = User.builder()
                .username(joinDTO.getUsername())
                .password(bCryptPasswordEncoder.encode(joinDTO.getPassword()))
                .role("ROLE_USER")

                .name(joinDTO.getName())
                .email(joinDTO.getEmail())
                .build();
        userRepository.save(member);
    }

    @Override
    public LoginResponseDTO authenticateAndGenerateTokens(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        // UsernamePasswordAuthenticationToken을 통해 인증 시도
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);
        PrincipleDetail authentication = (PrincipleDetail) authenticationManager.authenticate(authToken);

        // 인증 성공 시 토큰 생성 및 Redis 저장
        String accessToken = createTokensAndStoreInRedis(authentication);

        // 로그인 응답 객체 생성
        return LoginResponseDTO.builder()
                .username(username)
                .role(getRoleFromAuthentication(authentication))
                .build();
    }

    private String createTokensAndStoreInRedis(PrincipleDetail principleDetail) {
        String username = principleDetail.getUsername();
        String role = getRoleFromAuthentication(principleDetail);

        String accessToken = jwtUtil.createJWT("access", username, role, 3600000L); // 60분
        String refreshToken = jwtUtil.createJWT("refresh", username, role, 86400000L); // 24시간

        // Redis에 refreshToken 저장
        redisClient.setValue(username, refreshToken, 864000000L);

        return accessToken;
    }

    private String getRoleFromAuthentication(PrincipleDetail principleDetail) {
        Collection<? extends GrantedAuthority> authorities = principleDetail.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        return auth.getAuthority();
    }
}
