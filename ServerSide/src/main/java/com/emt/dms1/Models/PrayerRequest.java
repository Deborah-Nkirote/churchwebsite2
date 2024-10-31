package com.emt.dms1.Models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data

@AllArgsConstructor
@Table(name = "prayer_requests")
public class PrayerRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String request;

    private LocalDateTime createdAt;

    public PrayerRequest() {
        this.createdAt = LocalDateTime.now();
    }
}