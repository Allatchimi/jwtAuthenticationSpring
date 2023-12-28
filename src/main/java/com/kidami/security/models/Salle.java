package com.kidami.security.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "salle")
public class Salle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long salleId;
    private String salleName;

}
