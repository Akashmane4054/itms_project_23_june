package com.itms.product.service;

import java.util.Map;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.product.dto.EmployeeMasterDTO;

public interface EmployeeRegisterService {

	Map<String, Object> addUser(EmployeeMasterDTO employeeMasterDto)
			throws BussinessException, TechnicalException, ContractException;

}
