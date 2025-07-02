package com.itms.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TEAM_MASTER")
@Data
@NoArgsConstructor
public class TeamMaster {

	@Id
	@Column(name = "MODULE_CODE", nullable = false)
	private Long moduleCode;

	@Column(name = "SLA_FLAG", length = 2)
	private String slaFlag;

	@Column(name = "MODULE_NAME", length = 200)
	private String moduleName;

	@Column(name = "MODULE_TAG", length = 30)
	private String moduleTag;

	@Column(name = "OWNER", length = 200)
	private String owner;

	@Column(name = "SUBTEAM")
	private Long subteam;

	@Column(name = "EMP_ID", length = 20)
	private String empId;

	@Column(name = "VERTICAL_HEAD", length = 20)
	private String verticalHead;

	@Column(name = "IS_DELETE", length = 20)
	private String isDelete;

	@Column(name = "TAT", length = 20)
	private String tat;
}