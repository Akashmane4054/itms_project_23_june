package com.itms.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.BankDetails;

@Repository
public interface BankDetailsRepository extends JpaRepository<BankDetails, Long> {
	BankDetails findByBankName(String bankName);
}
