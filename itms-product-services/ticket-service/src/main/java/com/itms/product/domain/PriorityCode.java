package com.itms.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PRIORITY_CODE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriorityCode {

	@Id
	@Column(name = "PRIORITY_CODE")
	private Integer priorityCode;

	@Column(name = "PRIORITY_LEVEL", nullable = false, length = 15)
	private String priorityLevel;
}