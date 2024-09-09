package com.example.service;

import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.exception.GeneralException;
import com.example.domain.Node;
import com.example.domain.User;
import com.example.dto.JoinDTO;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
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
}
