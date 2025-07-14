package com.itms.product.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EmployeeMasterDTO {

    private String empId;
    private String empName;
    private String employeeOf;
    private Integer moduleCode;
    private Integer roleId;
    private Integer empStatus;
    private String emailId;
    private Integer loginCount;
    private String gender;
    private String email2;
    private String password;
    private Integer bDay;
    private Integer bMonth;
    private Integer bYear;
    private String mobile;
    private String blood;
    private String city;
    private String state;
    private String designation;
    private String project;
    private String bank;
    private String alternateNo;
    private String dob;
    private String doj;
    private LocalDateTime actionDate;
    private String predecessor;
    private String isNabard;
    private String isDeleted;
    private String shiftFlexibility;
    private String shiftName;
    private String leavingDate;
    private Integer maxLoginCount;
    private LocalDateTime lastFailedLoginTime;
    private String address;
    private String contractDate;
    private LocalDateTime lastPasswordChange;
    private String isMobileVerified;
    private String otp;
    private LocalDateTime otpExpiry;
    private LocalDateTime lastOtpValidation;
    private String isMobileUpdatePending;
    private LocalDateTime lastForgotValidation;
    private String groupLogin;
    private String logoutOld;
}
