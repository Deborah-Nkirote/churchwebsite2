package com.emt.dms1.Models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="Event ")
public class Events {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String Name;
    @Column
   // private String ImageUrl;
    private boolean deleted = false;
    @Column(name="Event date")
    private LocalDate date;
    @Column
    private String Imagetype;
    @Lob
    @Column(name = "image_data", nullable = true)
    private byte[] imageData;


}
