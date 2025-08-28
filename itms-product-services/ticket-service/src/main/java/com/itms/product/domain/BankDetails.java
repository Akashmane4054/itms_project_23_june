package com.itms.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "BANK_DETAILS")
@Data
public class BankDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer Id;

	@Column(name = "BANK_CODE", nullable = false)
	private Integer bankCode;

	@Column(name = "BANK_NAME", nullable = false, length = 100)
	private String bankName;

	@Column(name = "BANK_SHORT_NAME", nullable = false, length = 10)
	private String bankShortName;

	@Column(name = "STATE_CODE")
	private Integer stateCode;

	@Column(name = "BANK_GROUP", length = 20)
	private String bankGroup;

	@Column(name = "IS_DELETED", length = 20)
	private String isDeleted;

}
