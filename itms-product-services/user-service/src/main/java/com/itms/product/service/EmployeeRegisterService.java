package com.itms.product.service;

import java.util.Map;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.product.dto.EmployeeMasterDTO;
import com.itms.product.dto.RegisterMasterDTO;

public interface EmployeeRegisterService {

	Map<String, Object> addUser(EmployeeMasterDTO employeeMasterDto)
			throws BussinessException, TechnicalException, ContractException;

	Map<String, Object> registerUser(RegisterMasterDTO registerMasterDTO)
			throws BussinessException, TechnicalException, ContractException;

}
