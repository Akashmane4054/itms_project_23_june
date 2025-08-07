package com.itms.core.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.core.feignclient.userServiceFeignclient;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class userServiceUtil {

	private static final String CLASSNAME = userServiceUtil.class.getSimpleName();

	@Autowired
	private userServiceFeignclient userServiceFeignclient;

	public int getModuleCodeByTeamName(String teamName)
			throws ContractException, BussinessException, TechnicalException {

		log.info(LogUtil.startLog(CLASSNAME));

		try {
			Map<String, Object> map = ExceptionUtil
					.throwExceptionsIfPresent(userServiceFeignclient.getModuleCodeByteamName(teamName));
			ServiceUtil.checkServiceAvailability(map, Constants.USER_SERVICE);

			Object code = map.get("moduleCode");
			if (code instanceof Integer)
				return (int) code;
			if (code instanceof String)
				return Integer.parseInt((String) code);

			throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid moduleCode format");

		} catch (Exception e) {
			log.error(LogUtil.errorLog(e));
			throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.TECHNICAL_ERROR);
		} finally {
			log.info(LogUtil.exitLog(CLASSNAME));
		}
	}

	public String findVhIdByModuleCode(int moduleCode)
			throws ContractException, BussinessException, TechnicalException {

		log.info(LogUtil.startLog(CLASSNAME));

		try {
			Map<String, Object> map = ExceptionUtil
					.throwExceptionsIfPresent(userServiceFeignclient.findVhIdByModuleCode(moduleCode));
			ServiceUtil.checkServiceAvailability(map, Constants.USER_SERVICE);

			Object vhIdObj = map.get("vhId");

			if (vhIdObj != null) {
				return vhIdObj.toString(); // Convert to String safely
			} else {
				throw new BussinessException(HttpStatus.NOT_FOUND, "vhId not found for moduleCode: " + moduleCode);
			}

		} catch (BussinessException e) {
			throw e;
		} catch (Exception e) {
			log.error(LogUtil.errorLog(e));
			throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.TECHNICAL_ERROR);
		} finally {
			log.info(LogUtil.exitLog(CLASSNAME));
		}
	}

	public String findTlIdByModuleCode(int moduleCode)
			throws ContractException, BussinessException, TechnicalException {

		log.info(LogUtil.startLog(CLASSNAME));

		try {
			Map<String, Object> map = ExceptionUtil
					.throwExceptionsIfPresent(userServiceFeignclient.findTlIdByModuleCode(moduleCode));
			ServiceUtil.checkServiceAvailability(map, Constants.USER_SERVICE);

			Object TlIdObj = map.get("TlId");

			if (TlIdObj != null) {
				return TlIdObj.toString();
			} else {
				throw new BussinessException(HttpStatus.NOT_FOUND, "TlId not found for moduleCode: " + moduleCode);
			}

		} catch (BussinessException e) {
			throw e;
		} catch (Exception e) {
			log.error(LogUtil.errorLog(e));
			throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.TECHNICAL_ERROR);
		} finally {
			log.info(LogUtil.exitLog(CLASSNAME));
		}
	}

	public String findSpocTlByEmpId(String empId) throws ContractException, BussinessException, TechnicalException {

		log.info(LogUtil.startLog(CLASSNAME));

		try {
			// Call Feign client and check for exceptions
			Map<String, Object> map = ExceptionUtil
					.throwExceptionsIfPresent(userServiceFeignclient.findSpocTlByEmpId(empId));

			ServiceUtil.checkServiceAvailability(map, Constants.USER_SERVICE);

			// Fetch TL ID
			Object tlIdObj = map.get("tlId"); // changed key from "TlId" to "tlId"

			if (tlIdObj != null && !tlIdObj.toString().isEmpty()) {
				return tlIdObj.toString();
			} else {
				throw new BussinessException(HttpStatus.NOT_FOUND, "TL ID not found for EMP_ID: " + empId);
			}

		} catch (BussinessException e) {
			throw e;
		} catch (Exception e) {
			log.error(LogUtil.errorLog(e));
			throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.TECHNICAL_ERROR);
		} finally {
			log.info(LogUtil.exitLog(CLASSNAME));
		}
	}
	
	
	public Map<String, Object> tokenVerification(MultiValueMap<String, String> headers)
			throws BussinessException, ContractException, TechnicalException {
		log.info(LogUtil.startLog(CLASSNAME));
		Map<String, Object> map = new HashMap<>();
		try {

			map = ExceptionUtil.throwExceptionsIfPresent(userServiceFeignclient.getLoggedInUser(headers));
			ServiceUtil.checkServiceAvailability(map, Constants.USER_SERVICE);

		} catch (Exception e) {
			log.error(LogUtil.errorLog(e));
			throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.TECHNICAL_ERROR);

		}
		log.info(LogUtil.exitLog(CLASSNAME));
		return map;
	}

}
