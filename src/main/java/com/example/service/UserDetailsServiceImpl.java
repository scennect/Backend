package com.example.service;

import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.exception.GeneralException;
import com.example.domain.User;
import com.example.dto.PrincipleDetail;
import com.example.dto.UserDTO;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        UserDTO userDTO = UserDTO.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .name(user.getName())
                .role(user.getRole())
                .build();

        if(user != null) {
            return new PrincipleDetail(userDTO);
        }
        else {
            return null;
        }
    }
}
