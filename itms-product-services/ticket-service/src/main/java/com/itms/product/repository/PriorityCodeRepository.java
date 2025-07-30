package com.itms.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.PriorityCode;

@Repository
public interface PriorityCodeRepository extends JpaRepository<PriorityCode, Integer> {
	Optional<PriorityCode> findByPriorityLevel(String priorityLevel);
}
