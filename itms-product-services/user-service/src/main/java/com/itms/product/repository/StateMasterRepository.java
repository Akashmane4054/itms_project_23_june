package com.itms.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.StateMaster;

@Repository
public interface StateMasterRepository extends JpaRepository<StateMaster, Long> {
	Optional<StateMaster> findByStateName(String stateName);
}