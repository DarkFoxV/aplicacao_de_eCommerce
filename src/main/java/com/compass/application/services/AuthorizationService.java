package com.compass.application.services;

import com.compass.application.repositories.UserRepository;
import com.compass.application.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AuthorizationService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        try {
            return this.userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(""));
        } catch (UsernameNotFoundException e) {
            throw new ObjectNotFoundException("User not found: " + email);
        }
    }
}
