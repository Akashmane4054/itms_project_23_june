package com.itms.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.CaptchaDetails;

@Repository
public interface CapthaDetailsRepository extends JpaRepository<CaptchaDetails, Long> {

	CaptchaDetails findByUuidAndActiveTrue(String uuid);

}