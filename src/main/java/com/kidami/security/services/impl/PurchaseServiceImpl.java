package com.kidami.security.services.impl;

import com.kidami.security.dto.purchaseDTO.PurchaseDTO;
import com.kidami.security.enums.PurchaseStatus;
import com.kidami.security.mappers.PurchaseMapper;
import com.kidami.security.models.Cour;
import com.kidami.security.models.Enrollment;
import com.kidami.security.models.Purchase;
import com.kidami.security.models.User;
import com.kidami.security.repository.CourRepository;
import com.kidami.security.repository.EnrollmentRepository;
import com.kidami.security.repository.PurchaseRepository;
import com.kidami.security.repository.UserRepository;
import com.kidami.security.services.PurchaseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PurchaseMapper purchaseMapper;
    private final UserRepository userRepository;
    private final CourRepository courRepository;


    // Créer un nouvel achat
    @Override
    @Transactional
    public PurchaseDTO createPurchase(User user, Set<Cour> courses, BigDecimal amountTotal, String currency) {
        Purchase purchase = new Purchase();
        purchase.setBuyer(user);
        purchase.setCourses(courses); // on affecte directement ici
        purchase.setAmountTotal(amountTotal);
        purchase.setCurrency(currency);
        purchase.setStatus(PurchaseStatus.PENDING);
        purchase.setCreatedAt(LocalDateTime.now());

        Purchase saved = purchaseRepository.save(purchase);
        return purchaseMapper.toDTO(saved);
    }
    // Valider un achat et créer les enrollments

    @Transactional
    @Override
    public PurchaseDTO validatePurchase(Purchase purchase) {
        purchase.setStatus(PurchaseStatus.SUCCESS);
        Purchase purchase1 = purchaseRepository.save(purchase);

        // Créer les inscriptions pour chaque cours acheté
        for (Cour course : purchase.getCourses()) {
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(purchase.getBuyer());
            enrollment.setCour(course);
            enrollment.setPurchase(purchase);
            enrollmentRepository.save(enrollment);
        }
        return purchaseMapper.toDTO(purchase1);
    }

    @Override
    public List<PurchaseDTO> getUserPurchases(Long userId) {
         List<Purchase> purchases =  purchaseRepository.findByBuyerId(userId);
        return  purchases.stream()
                .map(purchaseMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public PurchaseDTO initiatePurchase(Long userId, Set<Long> courseIds, String currency) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Set<Cour> courses = courRepository.findAllById(courseIds)
                .stream().collect(Collectors.toSet());

        BigDecimal total = courses.stream()
                .map(Cour::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Purchase purchase = new Purchase();
        purchase.setBuyer(user);
        purchase.setCourses(courses);
        purchase.setAmountTotal(total);
        purchase.setCurrency(currency);
        purchase.setStatus(PurchaseStatus.PENDING);
        purchase.setCreatedAt(LocalDateTime.now());

        Purchase saved = purchaseRepository.save(purchase);

        return purchaseMapper.toDTO(saved);
    }

}
