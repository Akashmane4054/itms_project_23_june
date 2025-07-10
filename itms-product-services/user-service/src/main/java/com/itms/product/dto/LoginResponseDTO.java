package com.itms.product.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {
	private String token;
	private String empId;
	private String ageTicketAlert;
	private String mobileVerified;
	private String forcePassword;
	private String loginOtp;
	private String popupMessage;
	private String alreadyLoggedIn;
	private String maxlogin;
	private String roleType;
}
