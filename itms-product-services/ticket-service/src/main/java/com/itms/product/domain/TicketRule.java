package com.itms.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "TICKET_RULE")
@Data
public class TicketRule {

	@Id
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "EMP_ID", length = 20)
	private String empId;

	@Column(name = "TEAM_CODE")
	private Integer teamCode;

	@Column(name = "BANK_TYPE", length = 20)
	private String bankType;

	@Column(name = "SLA", length = 20)
	private String sla;

	@Column(name = "ISSUE", length = 1000)
	private String issue;
}