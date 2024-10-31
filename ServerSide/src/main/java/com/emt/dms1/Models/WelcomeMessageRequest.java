package com.emt.dms1.Models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity

public class WelcomeMessageRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    @Column(name = "welcome_message", nullable = false)
    private String message;
    @Column
    private String liveStreamUrl;
    // Getters and setters
}

