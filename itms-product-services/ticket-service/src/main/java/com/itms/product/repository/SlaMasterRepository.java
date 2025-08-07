package com.itms.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.SlaMaster;

import feign.Param;

@Repository
public interface SlaMasterRepository extends JpaRepository<SlaMaster, String> {

	@Query("SELECT s.slaTime FROM SlaMaster s WHERE s.slaId = :slaId")
	Integer findSlaTimeBySlaId(@Param("slaId") String slaId);

}
