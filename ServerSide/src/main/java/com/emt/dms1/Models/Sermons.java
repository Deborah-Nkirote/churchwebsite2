package com.emt.dms1.Models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="Sermons")
@NoArgsConstructor
@AllArgsConstructor
public class Sermons {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String title;
    private String date;
    private String videoUrl;

    @Lob
    private byte[] notesFile;

        // Getters and setters
    }
