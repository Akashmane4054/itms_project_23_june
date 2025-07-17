package com.itms.product.service;

import java.util.Map;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.product.dto.EmployeeMasterDTO;

public interface LoginService {

	Map<String, Object> loginWithLoginId(EmployeeMasterDTO employeeMasterDto)
			throws BussinessException, TechnicalException, ContractException;
	
	Map<String, Object> forgatePassword(EmployeeMasterDTO employeeMasterDto)
			throws BussinessException, TechnicalException, ContractException;

}
