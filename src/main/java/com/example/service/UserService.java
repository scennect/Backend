package com.example.service;

import com.example.domain.User;

public interface UserService {

    public User findUserByUsername(String username);

    public User checkIfUserExistsByEmail(String email);
}
