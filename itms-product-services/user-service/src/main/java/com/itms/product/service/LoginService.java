package com.itms.product.service;

import java.util.Map;

import org.springframework.util.MultiValueMap;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.product.domain.EmployeeMaster;
import com.itms.product.dto.EmployeeMasterDTO;

public interface LoginService {

	Map<String, Object> loginWithLoginId(EmployeeMasterDTO employeeMasterDto)
			throws BussinessException, TechnicalException, ContractException;

	Map<String, Object> forgatePassword(EmployeeMasterDTO employeeMasterDto)
			throws BussinessException, TechnicalException, ContractException;

	public Map<String, Object> getLoggedInUser(MultiValueMap<String, String> headers)
			throws BussinessException, TechnicalException, ContractException;

	EmployeeMasterDTO castToUDTO(EmployeeMaster employeeMaster);

	EmployeeMaster getLoggedInUserDTO() throws ContractException, TechnicalException, BussinessException;

}
