package com.itms.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itms.product.domain.BankDetails;

public interface BankDetailsRepository extends JpaRepository<BankDetails, Long> {
	Optional<BankDetails> findByBankName(String bankName);
}
