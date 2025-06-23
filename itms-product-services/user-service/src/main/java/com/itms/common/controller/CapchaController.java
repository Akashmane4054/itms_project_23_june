package com.itms.common.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itms.common.service.CapchaService;
import com.itms.core.exception.TechnicalException;
import com.itms.core.util.EndPointReference;
import com.itms.core.util.LogUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class CapchaController {

	private CapchaService captchaService;

//	@PostMapping({ EndPointReference.CAPTCHA_GENERATE })
//	public Map<String, Object> generateCaptcha(
//			@RequestParam(defaultValue = "COMPANY_PORTAL", value = "portalName", required = false) String portalName)
//			throws TechnicalException {
//		log.info(LogUtil.presentationLogger(EndPointReference.CAPTCHA_GENERATE));
//		return captchaService.generateCaptcha(portalName);
//	}

}
