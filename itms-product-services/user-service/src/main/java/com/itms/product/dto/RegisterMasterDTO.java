package com.itms.product.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class RegisterMasterDTO {

	private String empId;
	private String uName;
	private String gender;
	private String email;
	private String email2;
	private Integer bDay;
	private Integer bMonth;
	private Integer bYear;
	private Integer jDay;
	private Integer jMonth;
	private Integer jYear;
	private String mobile;
	private String extension;
	private String blood;
	private String team;
	private String city;
	private String state;
	private String designation;
	private String project;
	private List<String> bankList;
	private String alternateNo;
	private LocalDateTime lastLogin;
	private String dob;
	private String doj;
	private String notification;
	private String loggedIn;
	private String sessionId;
	private String address;
	private String contractDate;
}