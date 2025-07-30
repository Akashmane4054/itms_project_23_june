package com.itms.product.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "CALL_MASTER")
@Data
public class CallMaster {

	@Id
	@Column(name = "CALL_NO", nullable = false, length = 50)
	private String callNo;

	@Column(name = "CALL_BANK_NAME")
	private Integer callBankName;

	@Column(name = "CALL_BANK_BRANCH", length = 100)
	private String callBankBranch;

	@Column(name = "CALL_DESC", length = 4000)
	private String callDesc;

	@Column(name = "CALL_INITIATOR", length = 15)
	private String callInitiator;

	@Column(name = "CALL_DATE")
	private LocalDateTime callDate;

	@Column(name = "CALL_REPEAT_FLAG", length = 1)
	private String callRepeatFlag;

	@Column(name = "CALL_STATUS_CODE")
	private Integer callStatusCode;

	@Column(name = "CALL_ASSIGNED_MODULE_CODE")
	private Integer callAssignedModuleCode;

	@Column(name = "CALL_PRIORITY_CODE")
	private Integer callPriorityCode;

	@Column(name = "TICKET_TYPE", length = 200)
	private String ticketType;

	@Column(name = "CALL_ASSIGNTO", length = 150)
	private String callAssignTo;

	@Column(name = "ISSUE_DESCRIPTION_SHORT", length = 200)
	private String issueDescriptionShort;

	@Column(name = "SLA_LEVEL", length = 20)
	private String slaLevel;

	@Column(name = "CALL_PRIORITY", length = 20)
	private String callPriority;

	@Column(name = "CALL_SEVERITY_LEVEL", length = 20)
	private String callSeverityLevel;

	@Column(name = "CALL_ISSUE_REMARK", length = 4000)
	private String callIssueRemark;

	@Column(name = "ATTACHMENT_ID", length = 20)
	private String attachmentId;

	@Column(name = "NOTIFICATION", length = 25)
	private String notification;

	@Column(name = "CALL_DOWNTIME", length = 20)
	private String callDowntime;

	@Column(name = "CALL_DURATION", length = 200)
	private String callDuration;

	@Column(name = "IN_BUCKET_OF", length = 200)
	private String inBucketOf;

	@Column(name = "REQUIREMENT_TYPE", length = 200)
	private String requirementType;

	@Column(name = "EXTENSION_NO")
	private Long extensionNo;

	@Column(name = "CONFIRMATION", length = 20)
	private String confirmation;

	@Column(name = "LOCATION", length = 50)
	private String location;

	@Column(name = "DURATION", length = 20)
	private String duration;

	@Column(name = "EXPECTEDTIME")
	private LocalDateTime expectedTime;

	@Column(name = "NOTE_TITLE", length = 100)
	private String noteTitle;

	@Column(name = "NOTE_DESCRIPTION", length = 200)
	private String noteDescription;

	@Column(name = "NOTE_ADDEDBY", length = 200)
	private String noteAddedBy;

	@Column(name = "NOTIFICATION_SEEN_TIME")
	private LocalDateTime notificationSeenTime;

	@Column(name = "REJECT_REASON", length = 300)
	private String rejectReason;

	@Column(name = "PRIMARY_OWNER", length = 50)
	private String primaryOwner;

	@Column(name = "TRACKER", length = 20)
	private String tracker;

	@Column(name = "TICKET_APPLICATION", length = 5)
	private String ticketApplication;

	@Column(name = "TAT_AVAILABLE", length = 20)
	private String tatAvailable;

	@Column(name = "COMMENTS", length = 200)
	private String comments;

	@Column(name = "CLONE_REFERENCE", length = 2000)
	private String cloneReference;

	@Column(name = "DATA_UPDATE", length = 20)
	private String dataUpdate;

	@Column(name = "SUB_DEMAND_REFERENCE", length = 50)
	private String subDemandReference;

	@Column(name = "SUBDEMAND_EXP_TIMELINE", length = 60)
	private String subDemandExpTimeline;

	@Column(name = "CHARGEABLE", length = 10)
	private String chargeable;

	@Column(name = "CR_PRIORITY")
	private Integer crPriority;

	@Column(name = "FEASIBLE_FLAG", length = 50)
	private String feasibleFlag;

	@Column(name = "PROMOTER", length = 2000)
	private String promoter;
}