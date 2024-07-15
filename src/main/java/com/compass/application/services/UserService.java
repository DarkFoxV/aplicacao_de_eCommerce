package com.compass.application.services;

import com.compass.application.domain.User;
import com.compass.application.domain.enums.UserRoles;
import com.compass.application.dtos.UserDTO;
import com.compass.application.repositories.UserRepository;
import com.compass.application.services.exceptions.ObjectNotFoundException;
import com.compass.application.services.exceptions.ObjectAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User not found: " + id));
    }

    public User findByEmail(String email) {
        UserDetails userDetails = userRepository.findByEmail(email).orElseThrow(() -> new ObjectNotFoundException("User not found: " + email));
        return (User) userDetails;
    }

    public User save(UserDTO userDTO) {
        try {
            String encryptedPassword = new BCryptPasswordEncoder().encode(userDTO.password());
            User user = new User(null, userDTO.username(), userDTO.email(), encryptedPassword, UserRoles.USER);
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ObjectAlreadyExistsException("Email already registered");
        }
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }


}
