package com.emt.dms1.Services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;



import java.util.concurrent.TimeUnit;

public class JwtUtil {
    private static final long EXPIRATION_TIME = TimeUnit.HOURS.toMillis(1); // 1 hour expiration time
    private SecretKey secretKey;

    // Constructor to generate the key
    public JwtUtil() {
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    // Method to generate a password reset token
    public String generatePasswordResetToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Set your expiration time
                .signWith(secretKey) // Use the generated secure key
                .compact();
    }

    // Other methods as needed...
}

