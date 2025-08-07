package com.itms.product.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.core.util.EndPointReference;
import com.itms.core.util.LogUtil;
import com.itms.product.dto.EmployeeMasterDTO;
import com.itms.product.dto.RegisterMasterDTO;
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

	@PostMapping(EndPointReference.REGISTER_USER)
	public Map<String, Object> registerUser(@RequestBody RegisterMasterDTO registerMasterDTO)
			throws BussinessException, TechnicalException, ContractException {
		log.info(LogUtil.presentationLogger(EndPointReference.REGISTER_USER));
		return employeeRegisterService.registerUser(registerMasterDTO);
	}

	@PostMapping(EndPointReference.GET_MODULE_CODE_BY_TEAM_NAME)
	public Map<String, Object> getModuleCodeByteamName(@PathVariable String teamName)
			throws BussinessException, TechnicalException, ContractException {
		log.info(LogUtil.presentationLogger(EndPointReference.GET_MODULE_CODE_BY_TEAM_NAME));
		return employeeRegisterService.getModuleCodeByteamName(teamName);
	}

	@PostMapping(EndPointReference.FIND_VH_ID_BY_MODULE_CODE)
	public Map<String, Object> findVhIdByModuleCode(@PathVariable Integer moduleCode)
			throws BussinessException, TechnicalException, ContractException {
		log.info(LogUtil.presentationLogger(EndPointReference.FIND_VH_ID_BY_MODULE_CODE));
		return employeeRegisterService.findVhIdByModuleCode(moduleCode);
	}

	@PostMapping(EndPointReference.FIND_TL_ID_BY_MODULE_CODE)
	public Map<String, Object> findTlIdByModuleCode(@PathVariable Integer moduleCode)
			throws BussinessException, TechnicalException, ContractException {
		log.info(LogUtil.presentationLogger(EndPointReference.FIND_TL_ID_BY_MODULE_CODE));
		return employeeRegisterService.findTlIdByModuleCode(moduleCode);
	}

	@PostMapping(EndPointReference.FIND_SPOC_TL_BY_EMP_ID)
	public Map<String, Object> findSpocTlByEmpId(@PathVariable String empId)
			throws BussinessException, TechnicalException, ContractException {
		log.info(LogUtil.presentationLogger(EndPointReference.FIND_SPOC_TL_BY_EMP_ID));
		return employeeRegisterService.findSpocTlByEmpId(empId);
	}

}
