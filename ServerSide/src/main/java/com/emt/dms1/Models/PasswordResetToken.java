package com.emt.dms1.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    private String token;

    @Column(unique = true)
    private Long userId;

    private Date createdAt;
    private Date updatedAt;

    // Getters and setters
}
