package com.itms.product.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "CALL_HISTORY_MASTER")
@Data
public class CallHistoryMaster {

	@Id
	@Column(name = "CALL_HIS_ID", nullable = false)
	private Long callHisId;

	@Column(name = "CALL_NO", nullable = false, length = 15)
	private String callNo;

	@Column(name = "ASSIGNED_DATE")
	private LocalDateTime assignedDate;

	@Column(name = "FROM_MODULE")
	private Integer fromModule;

	@Column(name = "TO_MODULE")
	private Integer toModule;

	@Column(name = "EMP_ID", length = 15)
	private String empId;

	@Column(name = "CALL_REMARKS", length = 4000)
	private String callRemarks;

	@Column(name = "PRIORITY_CODE")
	private Integer priorityCode;

	@Column(name = "STATUS_CODE")
	private Integer statusCode;

	@Column(name = "TO_MEMBER", length = 15)
	private String toMember;

	@Column(name = "SLA_ID", length = 20)
	private String slaId;

	@Column(name = "HOLD_DATE")
	private LocalDateTime holdDate;

	@Column(name = "OFF_HOLD_DATE")
	private LocalDate offHoldDate;

	@Column(name = "REJECT_REASON", length = 300)
	private String rejectReason;

	@Column(name = "ATTACHMENT_ID")
	private Long attachmentId;
}