package com.compass.application.services;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.compass.application.domain.PasswordResetToken;
import com.compass.application.domain.User;
import com.compass.application.repositories.PasswordResetTokenRepository;
import com.compass.application.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    public void initiatePasswordReset(String email) {
        User user = userService.findByEmail(email);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.deleteAll();

        tokenRepository.save(resetToken);

        emailService.sendResetToken(email, token);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token).orElseThrow(() -> new ObjectNotFoundException("Invalid token"));

        if (resetToken.isExpired()) {
            throw new TokenExpiredException("Token has expired",resetToken.getExpiryDate().toInstant(ZoneOffset.of("GTM")));
        }

        User user = resetToken.getUser();
        String encryptedPassword = new BCryptPasswordEncoder().encode(newPassword);
        user.setPassword(encryptedPassword);
        userService.update(user);

        tokenRepository.delete(resetToken);
    }
}