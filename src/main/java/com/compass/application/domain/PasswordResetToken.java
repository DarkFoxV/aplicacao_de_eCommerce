package com.compass.application.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "PasswordResetTokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Value("${spring.mail.expiration_hours}")
    private static int EXPIRATION_HOURS;

    public PasswordResetToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusHours(EXPIRATION_HOURS);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    // getters and setters
}

