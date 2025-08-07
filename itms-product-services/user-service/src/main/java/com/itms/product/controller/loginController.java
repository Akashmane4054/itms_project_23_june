package com.itms.product.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.core.util.EndPointReference;
import com.itms.core.util.LogUtil;
import com.itms.product.dto.EmployeeMasterDTO;
import com.itms.product.service.LoginService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@CrossOrigin(origins = "*")
@RestController
public class loginController {

	@Autowired
	private LoginService loginService;

	@PostMapping(EndPointReference.LOGIN_WITH_LOGIN_ID)
	public Map<String, Object> loginWithLoginId(@RequestBody EmployeeMasterDTO employeeMasterDto)
			throws BussinessException, TechnicalException, ContractException {
		log.info(LogUtil.presentationLogger(EndPointReference.LOGIN_WITH_LOGIN_ID));
		return loginService.loginWithLoginId(employeeMasterDto);
	}

	@PostMapping(EndPointReference.FORGATE_PASSWORD)
	public Map<String, Object> forgatePassword(@RequestBody EmployeeMasterDTO employeeMasterDto)
			throws BussinessException, TechnicalException, ContractException {
		log.info(LogUtil.presentationLogger(EndPointReference.FORGATE_PASSWORD));
		return loginService.forgatePassword(employeeMasterDto);
	}

	@PostMapping(EndPointReference.GET_LOGGED_IN_USER)
	public Map<String, Object> getLoggedInUser(@RequestHeader MultiValueMap<String, String> headers)
			throws BussinessException, TechnicalException, ContractException {
		log.info(LogUtil.presentationLogger(EndPointReference.GET_LOGGED_IN_USER));
		return loginService.getLoggedInUser(headers);
	}

	@PostMapping(EndPointReference.GET_LOGGED_IN_USER_DTO)
	public EmployeeMasterDTO getLoggedInUserDTO() throws TechnicalException, BussinessException, ContractException {
		log.info(LogUtil.presentationLogger(EndPointReference.GET_LOGGED_IN_USER_DTO));
		return loginService.castToUDTO(loginService.getLoggedInUserDTO());
	}

}
