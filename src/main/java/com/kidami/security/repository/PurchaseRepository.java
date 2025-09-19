package com.kidami.security.repository;

import com.kidami.security.models.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
  List<Purchase> findByBuyerId(Long userId);
}