package com.itms.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeMasterDto {

	private Long id;

	private String empId;
	private String empName;
	private String password;
	private String employeeOf;
	private Integer moduleCode;
	private Integer roleId;
	private Integer empStatus;
	private Integer stateCode;
	private Integer bankCode;
	private String emailId;
	private Integer loginCount;
	private String gender;
	private String email2;
	private Integer bday;
	private Integer bmonth;
	private Integer byear;
	private Integer jday;
	private Integer jmonth;
	private Integer jyear;
	private String mobile;
	private String extension;
	private String blood;
	private String city;
	private String state;
	private String sq;
	private String sa;
	private String designation;
	private String project;
	private String bank;
	private String alternateNo;
	private String tlName;
}