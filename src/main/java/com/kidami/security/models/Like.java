package com.kidami.security.models;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "likes", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","cour_id"}))
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne @JoinColumn(name = "user_id") private User user;
    @ManyToOne @JoinColumn(name = "cour_id") private Cour course;
    private Instant createdAt = Instant.now();
}