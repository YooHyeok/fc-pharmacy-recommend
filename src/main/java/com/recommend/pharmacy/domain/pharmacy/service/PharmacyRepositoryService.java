package com.recommend.pharmacy.domain.pharmacy.service;

import com.recommend.pharmacy.domain.pharmacy.entity.Pharmacy;
import com.recommend.pharmacy.domain.pharmacy.repository.PharmacyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class PharmacyRepositoryService {
    private final PharmacyRepository pharmacyRepository;

    /**
     * Self Invocation Test
     */
    public void bar(List<Pharmacy> pharmacyList) {
        log.info("bar CurrentTransactionName: " + TransactionSynchronizationManager.getCurrentTransactionName());
        foo(pharmacyList);
    }

    /**
     * Self Invocation Test
     * bar에 의해 호출된다.
     */
    @Transactional
    public void foo(List<Pharmacy> pharmacyList) {
        log.info("foo CurrentTransactionName: " + TransactionSynchronizationManager.getCurrentTransactionName());
        pharmacyList.forEach(pharmacy -> {
            pharmacyRepository.save(pharmacy);
            throw new RuntimeException("Self Invocation Error"); // Rollback을 위한 예외 발생
        });
    }

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

    /**
     * Pharmacy - 약국 API CSV파일 DTO 변환 후 MariaDB 저장시 사용
     */
    public List<Pharmacy> saveAll(List<Pharmacy> pharmacyDtoList) {
        if (CollectionUtils.isEmpty(pharmacyDtoList)) return Collections.emptyList();
        return pharmacyRepository.saveAll(pharmacyDtoList);
    }

    /**
     * 고객이 주소정보를 입력하면 가까운 약국을 찾아주기 위해 모든 약국리스트 검색
     */
    @Transactional(readOnly = true) // 읽기전용 성능향상 - Entity와 Snap샷간의 DirtyChecking 비교 비용 감소
    public List<Pharmacy> findAll() {
        return pharmacyRepository.findAll();
    }
}
