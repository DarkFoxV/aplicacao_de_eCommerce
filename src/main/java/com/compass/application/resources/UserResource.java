package com.compass.application.resources;

import com.compass.application.domain.User;
import com.compass.application.dtos.UserDTO;
import com.compass.application.security.TokenService;
import com.compass.application.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("v1/users")
public class UserResource {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserDTO userDTO) {
        User user = userService.save(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody @Valid UserDTO userDTO) {
        try {
            User user = userService.findByEmail(userDTO.email());
            UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(userDTO.email(), userDTO.password());
            var auth = authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((User) auth.getPrincipal());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
