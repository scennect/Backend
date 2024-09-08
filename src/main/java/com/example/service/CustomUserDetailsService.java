package com.example.service;

import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.exception.GeneralException;
import com.example.domain.User;
import com.example.dto.CustomUserDetails;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        if(user != null) {
            return new CustomUserDetails(user);
        }
        else {
            return null;
        }
    }
}
