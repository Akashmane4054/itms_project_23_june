package com.itms.product.service;

import java.util.Map;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.product.dto.EmployeeMasterDto;

public interface LoginService {

//	Map<String, Object> authenticateUserWithLoginId(EmployeeMaster employeeMaster,
//			HttpServletRequest httpServletRequest) throws TechnicalException, BussinessException, ContractException;
//
//	
	Map<String, Object> loginWithLoginId(EmployeeMasterDto employeeMasterDto)
			throws BussinessException, TechnicalException, ContractException;

}
