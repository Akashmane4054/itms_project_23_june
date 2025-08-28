package com.itms.product.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ServiceTicketDTO {
	private String bankRegion;
	private String regBody;
	private String circularId;
	private String circularDate;
	private String circularExpectedDate;
	private String brType;
	private String regExpectedDate;
	private String brDescription;
	private String brdBusiness;
	private String userId;
	private String userName;
	private String userTeam;
	private int userRole;
	private String ticketType;
	private String IRnumber;
	private String bankname;
	private String assigntoCRE;
	private String issueDescription;
	private String sla;
	private String rdoNew;
	private String priority;
	private String severityLevel;
	private String issue;
	private String issueRemark;
	private String extension;
	private String tracker;
	private String reviewTicket;
	private String cloneRefId;
	private String chargeable;
	private MultipartFile brdBusinessFile;
	private MultipartFile circularUploadFile;
}