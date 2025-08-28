package com.itms.core.feignclient;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.core.fallbackFactory.userServiceFallbackFactory;
import com.itms.core.util.EndPointReference;

@FeignClient(name = "user-service", url = "http://localhost:9002", fallbackFactory = userServiceFallbackFactory.class)
@Component
public interface userServiceFeignclient {

	@PostMapping(EndPointReference.USER_SERVICE + EndPointReference.GET_MODULE_CODE_BY_TEAM_NAME)
	public Map<String, Object> getModuleCodeByteamName(@RequestParam("teamName") String teamName)
			throws BussinessException, ContractException, TechnicalException;

	@PostMapping(EndPointReference.USER_SERVICE + EndPointReference.FIND_VH_ID_BY_MODULE_CODE)
	public Map<String, Object> findVhIdByModuleCode(@RequestParam("moduleCode") Integer moduleCode)
			throws BussinessException, ContractException, TechnicalException;

	@PostMapping(EndPointReference.USER_SERVICE + EndPointReference.FIND_TL_ID_BY_MODULE_CODE)
	public Map<String, Object> findTlIdByModuleCode(@RequestParam("moduleCode") Integer moduleCode)
			throws BussinessException, ContractException, TechnicalException;

	@PostMapping(EndPointReference.USER_SERVICE + EndPointReference.FIND_SPOC_TL_BY_EMP_ID)
	public Map<String, Object> findSpocTlByEmpId(@RequestParam("empId") String empId)
			throws BussinessException, ContractException, TechnicalException;

	@PostMapping(EndPointReference.GET_LOGGED_IN_USER)
	public Map<String, Object> getLoggedInUser(@RequestHeader MultiValueMap<String, Object> headers);

}
