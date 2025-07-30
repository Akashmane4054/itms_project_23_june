package com.itms.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "SLA_MASTER")
@Data
public class SlaMaster {

	@Id
	@Column(name = "SLA_ID", nullable = false, length = 10)
	private String slaId;

	@Column(name = "SLA_TYPE", length = 1000)
	private String slaType;

	@Column(name = "SLA_TIME")
	private Integer slaTime;
}