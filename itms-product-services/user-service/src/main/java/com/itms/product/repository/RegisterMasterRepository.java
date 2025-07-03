package com.itms.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.RegisterMaster;

@Repository
public interface RegisterMasterRepository extends JpaRepository<RegisterMaster, String> {
	
	
}