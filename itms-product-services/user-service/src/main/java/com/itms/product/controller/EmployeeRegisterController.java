package com.itms.product.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.core.util.EndPointReference;
import com.itms.core.util.LogUtil;
import com.itms.product.dto.EmployeeMasterDTO;
import com.itms.product.service.EmployeeRegisterService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class EmployeeRegisterController {

	// addUser api

	// registerUser api

	@Autowired
	private EmployeeRegisterService employeeRegisterService;

	@PostMapping(EndPointReference.ADD_USER)
	public Map<String, Object> addUser(@RequestBody EmployeeMasterDTO employeeMasterDto)
			throws BussinessException, TechnicalException, ContractException {
		log.info(LogUtil.presentationLogger(EndPointReference.ADD_USER));
		return employeeRegisterService.addUser(employeeMasterDto);
	}

}
