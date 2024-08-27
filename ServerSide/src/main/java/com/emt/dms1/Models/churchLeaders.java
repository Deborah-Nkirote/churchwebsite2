package com.emt.dms1.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Leaders")
@Entity


public class churchLeaders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String Name;
    @Column
    private String Designation;
    @Column
    private String BioData;
    @Column
    private long PhoneNo;
    @Column
    private String Imagetype;
    @Lob
    @Column(name = "image_data", nullable = true)
    private byte[] imageData;

}