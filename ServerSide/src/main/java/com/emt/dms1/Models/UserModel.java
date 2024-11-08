package com.emt.dms1.Models;


import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table
@Builder

public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    @Column(name = "email", unique = true, nullable = false)
    private String email;



    private String password;
    private Long phoneNumber;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RegisterRequest {

        private String username;
        private String emailAddress;
        private String password;
        private String phoneNumber;



    }
}