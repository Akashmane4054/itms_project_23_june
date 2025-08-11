package com.itms.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "issue_list")
@Data
public class IssueList {

	@Id
	@Column(name = "TEAM_CODE", length = 20)
	private Integer teamCode;

	@Column(name = "TEAM_NAME", nullable = false, length = 30)
	private String teamName;

	@Column(name = "OLD_ISSUE", length = 500)
	private String oldIssue;

	@Column(name = "ISSUE", nullable = false, length = 500)
	private String issue;

	@Column(name = "SLA_LEVEL", length = 20)
	private String slaLevel;

	@Column(name = "SLA_TIME", length = 100)
	private String slaTime;

	@Column(name = "VISIBLE", nullable = false, length = 20)
	private String visible;

	@Column(name = "CR_FLAG", length = 20)
	private String crFlag;

	@Column(name = "FLAG24HOURS", length = 20)
	private String flag24Hours;

}
