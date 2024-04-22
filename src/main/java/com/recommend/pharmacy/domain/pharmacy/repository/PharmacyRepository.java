package com.recommend.pharmacy.domain.pharmacy.repository;

import com.recommend.pharmacy.domain.pharmacy.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
}
