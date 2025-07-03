package com.itms.product.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER_ACTION_HISTORY")
@Data
@NoArgsConstructor
public class UserActionHistory {

	@Id
	@Column(name = "ACTION_ID", length = 200)
	private String actionId; // Since it's VARCHAR2(200), assuming you'll set it manually (like a UUID or
								// sequence)

	@Column(name = "EMP_ID", length = 200)
	private String empId;

	@Column(name = "EMPLOYEE_OF", length = 200)
	private String employeeOf;

	@Column(name = "MODULE_CODE", length = 20)
	private String moduleCode;

	@Column(name = "PASSWORD", length = 100)
	private String password;

	@Column(name = "ROLE_ID", length = 20)
	private String roleId;

	@Column(name = "ACTION", length = 100)
	private String action;

	@Column(name = "ACTION_DATE")
	private LocalDateTime actionDate;

	@Column(name = "EMP_TL", length = 20)
	private String empTl;

	@Column(name = "EN_PASSWORD", length = 256)
	private String enPassword;
}
