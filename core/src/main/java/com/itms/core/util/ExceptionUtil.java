package com.itms.core.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.itms.core.dto.ErrorResponse;
import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionUtil {

	private ExceptionUtil() {
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> extractExceptionMessage(String exception) {
		log.info(LogUtil.startLog(ExceptionUtil.class.getName()));
		Map<String, Object> map = new HashMap<>();
		if (exception.contains("content:")) {
			log.info(exception);
			String msg = exception.split("content:")[1];
			ObjectMapper mapper = new ObjectMapper();
			try {
				map = mapper.readValue(msg, Map.class);
			} catch (IOException e) {
				log.info(e.getMessage());
			}
		}
		log.info(LogUtil.exitLog(ExceptionUtil.class.getName()));
		return map;
	}

	public static Map<String, Object> throwExceptionsIfPresent(Map<String, Object> map)
			throws BussinessException, ContractException, TechnicalException {
		log.info(LogUtil.startLog(ExceptionUtil.class.getName()));
		Gson gson = new Gson();
		log.info("return map:{}", gson.toJson(map));

		if (map.get(Constants.ERROR) != null) {
			log.info("error: " + map.get(Constants.ERROR));
			ErrorResponse error = new ErrorResponse();
			String json = gson.toJson(map.get(Constants.ERROR));
			log.info("json: {}", json);
			ObjectMapper mapper = new ObjectMapper();
			try {
				error = mapper.readValue(json, new TypeReference<ErrorResponse>() {
				});
			} catch (IOException e) {
				log.error(e.getLocalizedMessage());
			}
			log.info("error: " + error);
			String errorCode = error.getErrorCode();
			log.info("errorCode: {}", errorCode);
			int statusCode = Integer.parseInt(errorCode.split(" ")[0]);
			log.info("statusCode: {}", statusCode);
			String instanceOf = error.getInstanceOf();
			if (instanceOf != null) {
				switch (instanceOf) {
				case "BussinessException":
					throw new BussinessException(HttpStatus.valueOf(statusCode), error.getMessage());
				case "ContractException":
					throw new ContractException(HttpStatus.valueOf(statusCode), error.getMessage());
				case "TechnicalException":
					throw new TechnicalException(HttpStatus.valueOf(statusCode), error.getMessage());
				default:
					log.info("Unhandled instanceOf: {}", instanceOf);
					break;
				}
			} else {
				log.info("No instanceOf found in error — skipping exception throw.");
			}
		}
		log.info(LogUtil.exitLog(ExceptionUtil.class.getName()));
		return map;
	}
}
