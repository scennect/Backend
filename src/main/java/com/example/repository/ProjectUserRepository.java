package com.example.repository;

import com.example.domain.Project;
import com.example.domain.ProjectUser;
import com.example.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
    boolean existsByProjectAndUser(Project project, User user);
}
