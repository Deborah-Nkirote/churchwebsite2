package com.emt.dms1.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Admin")
public class AdminModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "welcome_message", nullable = false) // Customize column name and constraints
    private String welcomeMessage;


    private String liveStreamUrl;

    // Additional methods or validations can be added here if needed
}
