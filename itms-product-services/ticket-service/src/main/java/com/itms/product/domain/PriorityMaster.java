package com.itms.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "PRIORITY_MASTER")
@Data
public class PriorityMaster {

	@Id
	@Column(name = "PRIORITY_CODE")
	private Integer priorityCode;

	@Column(name = "PRIORITY_LEVEL", length = 15)
	private String priorityLevel;
}
