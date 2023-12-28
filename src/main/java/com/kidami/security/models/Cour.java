package com.kidami.security.models;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name= "cour")
public class Cour {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    @ManyToOne
    private Category category;
    private String content;
    private String classe;

}
