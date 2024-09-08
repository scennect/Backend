//package com.example.service;
//
//import com.example.domain.User;
//import com.example.dto.oauth2DTO.CustomOAuth2User;
//import com.example.dto.oauth2DTO.GoogleResponse;
//import com.example.dto.oauth2DTO.OAuth2Response;
//import com.example.dto.UserDTO;
//import com.example.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {
//
//    private final UserRepository userRepository;
//
//    //OAuth2UserRequest는 OAuth2 공급자로부터 사용자 정보를 가져오기 위한 정보를 포함하는 클래스
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//
//        String registrationId = userRequest.getClientRegistration().getRegistrationId();
//        OAuth2Response oAuth2Response = null;
//
//        if(registrationId.equals("google")) {
////            System.out.println("구글 로그인 요청");
//            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
//        }
//        else{
//            return null;
//        }
//
//        String username = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
//
//        Optional<User> existUser = userRepository.findByUsername(username);
//
//        if(existUser.isEmpty()) {
//            User user = User.builder()
//                    .username(username)
//                    .name(oAuth2Response.getName())
//                    .email(oAuth2Response.getEmail())
//                    .role("ROLE_USER")
//                    .build();
//            userRepository.save(user);
//
//            UserDTO userDto = UserDTO.builder()
//                    .username(username)
//                    .name(oAuth2Response.getName())
//                    .role("ROLE_USER")
//                    .build();
//
//            return new CustomOAuth2User(userDto);
//        }
//        else {
////            existUser.updateName(oAuth2Response.getName());
////            업데이트 진행
//            UserDTO userDto = UserDTO.builder()
//                    .username(existUser.get().getUsername())
//                    .name((oAuth2Response.getName()))
//                    .role("ROLE_USER")
//                    .build();
//            return new CustomOAuth2User(userDto);
//        }
//
//    }
//}
