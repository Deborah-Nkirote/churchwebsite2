package com.emt.dms1.Models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String residence;
    private String phoneNumber;
    private boolean isMember;
    private LocalDate date; // true for members, false for visitors

    // Constructors, Getters and Setters

    public AttendanceRecord() {}

    public AttendanceRecord(String fullName, String residence, String phoneNumber, boolean isMember) {
        this.fullName = fullName;
        this.residence = residence;
        this.phoneNumber = phoneNumber;
        this.isMember = isMember;

    }

    // Getters and Setters

    // Override equals() and hashCode() to ensure each person can only appear once
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceRecord that = (AttendanceRecord) o;
        return phoneNumber.equals(that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return phoneNumber.hashCode();
    }
}
