package com.emt.dms1.Models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(targetEntity = UserModel.class, optional = false)
    @JoinColumn(nullable = false, name = "user_id")
    private UserModel user;

    private LocalDateTime ExpirationDate;
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(ExpirationDate);
    }
}
