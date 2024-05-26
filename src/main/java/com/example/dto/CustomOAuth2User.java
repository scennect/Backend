package com.example.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Builder
@Getter
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final UserDTO userDto;


//    @Override
//    public <A> A getAttribute(String name) {
//        return OAuth2User.super.getAttribute(name);
//    }

    @Override
    public Map<String, Object> getAttributes() {

        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return userDto.getRole();
            }
        });
        return collection;
    }

    @Override
    public String getName() {
        return userDto.getName();
    }

    public String getUsername() {
        return userDto.getUsername();
    }

    public String getRole(){
        return userDto.getRole();
    }
}
