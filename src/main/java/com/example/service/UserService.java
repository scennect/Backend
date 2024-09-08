package com.example.service;

import com.example.domain.User;
import com.example.dto.JoinDTO;

public interface UserService {

    public User findUserByUsername(String username);

    public User checkIfUserExistsByEmail(String email);

    public User findUserByEmail(String email);

    public void join(JoinDTO joinDTO);
}
