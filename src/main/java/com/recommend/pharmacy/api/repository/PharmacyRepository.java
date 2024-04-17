package com.recommend.pharmacy.api.repository;

import com.recommend.pharmacy.api.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
}
