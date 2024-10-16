package com.example.service;

import com.example.domain.User;
import com.example.dto.JoinDTO;
import com.example.dto.LoginResponseDTO;
import com.example.dto.PrincipleDetail;
import com.example.dto.UserLoginDTO;

public interface UserService {

    public User findUserByUsername(String username);

    public User checkIfUserExistsByEmail(String email);

    public User findUserByEmail(String email);

    public User loadMemberByPrincipleDetail(PrincipleDetail principalDetail);

    public void join(JoinDTO joinDTO);

    public LoginResponseDTO authenticateAndGenerateTokens(UserLoginDTO userLoginDTO);
}
