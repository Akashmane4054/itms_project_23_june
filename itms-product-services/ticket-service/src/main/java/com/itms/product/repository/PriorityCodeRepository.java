package com.itms.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.PriorityCode;

@Repository
public interface PriorityCodeRepository extends JpaRepository<PriorityCode, Integer> {
	PriorityCode findByPriorityLevel(String priorityLevel);
}
