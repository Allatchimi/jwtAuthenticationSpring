// NOUVELLE ENTITÉ: Enrollment.java
package com.kidami.security.models;


import com.kidami.security.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "enrollments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "cour_id", nullable = false)
    private Cour cour;

    private LocalDateTime enrolledAt = LocalDateTime.now();

    // Pour savoir d’où vient l’inscription
    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;
}
