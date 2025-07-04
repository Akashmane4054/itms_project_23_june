package com.itms.product.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employee_master")
public class EmployeeMaster {

	@Id
	@Column(name = "EMP_ID", length = 15, nullable = false)
	private String empId;

	@Column(name = "EMP_NAME", length = 100)
	private String empName;

	@Column(name = "PASSWORD", length = 50)
	private String password;

	@Column(name = "EMPLOYEE_OF", length = 150)
	private String employeeOf;

	@Column(name = "MODULE_CODE")
	private Integer moduleCode;

	@Column(name = "ROLE_ID")
	private Integer roleId;

	@Column(name = "EMP_STATUS")
	private Integer empStatus;

	@Column(name = "STATE_CODE")
	private Long stateCode;

	@Column(name = "BANK_CODE")
	private Integer bankCode;

	@Column(name = "EMAIL_ID", length = 150)
	private String emailId;

	@Column(name = "LOGIN_COUNT")
	private Integer loginCount;

	@Column(name = "GENDER", length = 10)
	private String gender;

	@Column(name = "EMAIL2", length = 40)
	private String email2;

	@Column(name = "BDAY")
	private Integer bDay;

	@Column(name = "BMONTH")
	private Integer bMonth;

	@Column(name = "BYEAR")
	private Integer bYear;

	@Column(name = "JDAY")
	private Integer jDay;

	@Column(name = "JMONTH")
	private Integer jMonth;

	@Column(name = "JYEAR")
	private Integer jYear;

	@Column(name = "MOBILE", length = 20)
	private String mobile;

	@Column(name = "EXTENSION", length = 20)
	private String extension;

	@Column(name = "BLOOD", length = 20)
	private String blood;

	@Column(name = "CITY", length = 49)
	private String city;

	@Column(name = "STATE", length = 20)
	private String state;

	@Column(name = "SA", length = 300)
	private String sa;

	@Column(name = "SQ", length = 300)
	private String sq;

	@Column(name = "DESIGNATION", length = 50)
	private String designation;

	@Column(name = "PROJECT", length = 20)
	private String project;

	@Column(name = "BANK", length = 300)
	private String bank;

	@Column(name = "ALTERNATE_NO", length = 20)
	private String alternateNo;

	@Column(name = "DOB", length = 50)
	private String dob;

	@Column(name = "DOJ", length = 50)
	private String doj;

	@Column(name = "ACTION_DATE")
	private LocalDateTime actionDate;

	@Column(name = "PREDECESSOR", length = 20)
	private String predecessor;

	@Column(name = "IS_NABARD", length = 5)
	private String isNabard;

	@Column(name = "IS_DELETED", length = 2)
	private String isDeleted;

	@Column(name = "ALTERNATE_N", length = 255)
	private String alternateN;

	@Column(name = "SHIFT_FLEXIBILITY", length = 5)
	private String shiftFlexibility;

	@Column(name = "SHIFT_NAME", length = 20)
	private String shiftName;

	@Column(name = "LEAVING_DATE", length = 20)
	private String leavingDate;

	@Column(name = "MAXLOGINCOUNT")
	private Integer maxLoginCount;

	@Column(name = "LASTFAILEDLOGINTIME")
	private LocalDateTime lastFailedLoginTime;

	@Column(name = "ADDRESS", length = 300)
	private String address;

	@Column(name = "CONTRACT_DATE", length = 20)
	private String contractDate;

	@Column(name = "EN_PASSWORD", length = 256)
	private String enPassword;

	@Column(name = "LAST_PASSWORD_CHANGE")
	private LocalDateTime lastPasswordChange;

	@Column(name = "ISMOBILEVERIFIED", length = 2)
	private String isMobileVerified;

	@Column(name = "OTP", length = 6)
	private String otp;

	@Column(name = "OTPEXPIRY")
	private LocalDateTime otpExpiry;

	@Column(name = "LASTOTPVALIDATION")
	private LocalDateTime lastOtpValidation;

	@Column(name = "ISMOBILEUPDATEPENDING", length = 2)
	private String isMobileUpdatePending;

	@Column(name = "LASTFORGOTVALIDATION")
	private LocalDateTime lastForgotValidation;

	@Column(name = "P1", length = 50)
	private String p1;

	@Column(name = "P2", length = 50)
	private String p2;
}