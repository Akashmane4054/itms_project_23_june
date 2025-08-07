package com.itms.core.fallback;

import java.util.Map;

import org.springframework.util.MultiValueMap;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.core.feignclient.userServiceFeignclient;

public class userServiceFallback implements userServiceFeignclient {

	private final Throwable cause;

	public userServiceFallback(Throwable cause) {
		this.cause = cause;
	}

	@Override
	public Map<String, Object> getModuleCodeByteamName(String teamName)
			throws BussinessException, ContractException, TechnicalException {
		return null;
	}

	@Override
	public Map<String, Object> findVhIdByModuleCode(Integer moduleCode)
			throws BussinessException, ContractException, TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> findTlIdByModuleCode(Integer moduleCode)
			throws BussinessException, ContractException, TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> findSpocTlByEmpId(String empId)
			throws BussinessException, ContractException, TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getLoggedInUser(MultiValueMap<String, String> headers) {
		// TODO Auto-generated method stub
		return null;
	}

}
