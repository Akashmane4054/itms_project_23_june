package com.itms.common.service;

import java.util.Map;

import com.itms.common.domain.EmployeeMaster;
import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;

public interface LoginService {

//	Map<String, Object> authenticateUserWithLoginId(EmployeeMaster employeeMaster,
//			HttpServletRequest httpServletRequest) throws TechnicalException, BussinessException, ContractException;
//
//	
	Map<String, Object> loginWithLoginId(EmployeeMaster employeeMaster)
			throws BussinessException, TechnicalException, ContractException;

}
