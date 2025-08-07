package com.itms.core.util;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.http.HttpStatus;

import com.itms.core.exception.TechnicalException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceUtil {
	private static final String CLASSNAME = ServiceUtil.class.getSimpleName();

	private static final String ERROR = "error";

	private ServiceUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static void checkServiceAvailability(Map<String, Object> map, String serviceName) throws TechnicalException {
		log.info(LogUtil.startLog(CLASSNAME));
		if (MapUtils.isEmpty(map)) {
			String message = "Check if " + serviceName + " is running";
			log.error(message);
			throw new TechnicalException(HttpStatus.FAILED_DEPENDENCY, message, serviceName);
		} else if (MapUtils.isNotEmpty(map) && map.containsKey(ERROR) && map.get(ERROR) != null) {
			throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, map.get(ERROR).toString(), serviceName);
		}
		log.info(LogUtil.exitLog(CLASSNAME));
	}

}
