package com.recommend.pharmacy.domain.direction.repository;

import com.recommend.pharmacy.domain.direction.entity.Direction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectionRepository extends JpaRepository<Direction, Long> {
}
