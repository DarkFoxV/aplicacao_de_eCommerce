package com.compass.application.repositories;

import com.compass.application.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional <UserDetails> findByEmail(String email);
}
