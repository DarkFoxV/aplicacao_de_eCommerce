package com.compass.application.services;

import com.compass.application.domain.User;
import com.compass.application.domain.enums.UserRoles;
import com.compass.application.dtos.UserDTO;
import com.compass.application.repositories.UserRepository;
import com.compass.application.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User not found: " + id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ObjectNotFoundException("User not found: " + email));
    }

    public User save(UserDTO userDTO) {
        User user = new User(null, userDTO.username(), userDTO.email(), userDTO.password(), UserRoles.USER);
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }


}
