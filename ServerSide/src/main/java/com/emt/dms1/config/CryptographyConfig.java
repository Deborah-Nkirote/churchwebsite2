package com.emt.dms1.config;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class CryptographyConfig {

    @Bean
    public SecretKey secretKey() {
        // Generate a secure random key for HS512 algorithm
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }
}
