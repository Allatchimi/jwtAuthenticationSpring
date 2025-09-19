package com.kidami.security.models;

import com.kidami.security.enums.PurchaseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "purchases")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qui a acheté
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User buyer;

    // Liste des cours achetés
    @ManyToMany
    @JoinTable(
            name = "purchase_courses",
            joinColumns = @JoinColumn(name = "purchase_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Cour> courses = new HashSet<>();

    private BigDecimal amountTotal;
    private String currency;

    @Enumerated(EnumType.STRING)
    private PurchaseStatus status; // PENDING, SUCCESS, FAILED, REFUNDED

    private LocalDateTime createdAt = LocalDateTime.now();

    // Un achat peut avoir plusieurs paiements (ex. tentative échouée puis réussie)
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();
}

