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
@Table(name = "register_master")
public class RegisterMaster {

	@Id
	@Column(name = "EMPID", length = 20, nullable = false)
	private String empId;

	@Column(name = "UNAME", length = 100)
	private String uName;

	@Column(name = "GENDER", length = 10)
	private String gender;

	@Column(name = "NPASS", length = 255)
	private String nPass;

	@Column(name = "EMAIL", length = 40)
	private String email;

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

	@Column(name = "TEAM", length = 20)
	private String team;

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

	@Column(name = "PROJECT", length = 50)
	private String project;

	@Column(name = "BANK", length = 300)
	private String bank;

	@Column(name = "ALTERNATE_NO", length = 20)
	private String alternateNo;

	@Column(name = "LAST_LOGIN")
	private LocalDateTime lastLogin;

	@Column(name = "DOB", length = 50)
	private String dob;

	@Column(name = "DOJ", length = 50)
	private String doj;

	@Column(name = "NOTIFICATION", length = 20)
	private String notification;

	@Column(name = "LOGGED_IN", length = 5)
	private String loggedIn;

	@Column(name = "SESSION_ID", length = 50)
	private String sessionId;

	@Column(name = "ADDRESS", length = 300)
	private String address;

	@Column(name = "CONTRACT_DATE", length = 20)
	private String contractDate;

	@Column(name = "EN_PASSWORD", length = 256)
	private String enPassword;
}