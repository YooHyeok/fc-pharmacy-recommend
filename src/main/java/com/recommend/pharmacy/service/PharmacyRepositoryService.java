package com.recommend.pharmacy.service;

import com.recommend.pharmacy.api.entity.Pharmacy;
import com.recommend.pharmacy.api.repository.PharmacyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class PharmacyRepositoryService {
    private final PharmacyRepository pharmacyRepository;

    @Transactional
    public void updateAddress(Long id, String address) {
        Pharmacy pharmacy = pharmacyRepository.findById(id).orElse(null);
        if (Objects.isNull(pharmacy)) {
            log.error("[PharamacyRepositoryService updateAddress] not found id: {}", id);
            return;
        }
        pharmacy.changePharmacyAddress(address);
    }

    // for test
    public void updateAddressWithoutTransaction(Long id, String address) {
        Pharmacy pharmacy = pharmacyRepository.findById(id).orElse(null);
        if (Objects.isNull(pharmacy)) {
            log.error("[PharamacyRepositoryService updateAddress] not found id: {}", id);
            return;
        }
        pharmacy.changePharmacyAddress(address);
    }
}
