//package com.itms.product.serviceImpl;
//
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//import java.util.UUID;
//
//import javax.imageio.ImageIO;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//
//import com.google.code.kaptcha.impl.DefaultKaptcha;
//import com.google.code.kaptcha.util.Config;
//import com.itms.core.exception.TechnicalException;
//import com.itms.core.util.Constants;
//import com.itms.core.util.LogUtil;
//import com.itms.product.domain.CaptchaDetails;
//import com.itms.product.dto.CaptchaResponseDTO;
//import com.itms.product.repository.CapthaDetailsRepository;
//import com.itms.product.service.CapchaService;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Service
//public class CapchaServiceImpl implements CapchaService {
//
//	private final DefaultKaptcha captchaProducer;
//	private static final Map<String, String> PORTAL_COLORS = new HashMap<>();
//
//	@Autowired
//	private CapthaDetailsRepository capthaDetailsRepository;
//
//	static {
//		PORTAL_COLORS.put("LIFEMART_SUPER_ADMIN", "216,30,33"); // #d81e21 (Red)
//		PORTAL_COLORS.put("LIFEMART_COMPANY", "216,30,33"); // #d81e21 (Red)
//		PORTAL_COLORS.put("RESPONDENT_PORTAL", "0,203,68"); // #00cb44 (Green)
//		PORTAL_COLORS.put("COMPANY_PORTAL", "255,129,0"); // #ff8100 (Orange)
//		PORTAL_COLORS.put("SUPER_ADMIN", "255,129,0"); // #ff8100 (Orange)
//	}
//
//	public CapchaServiceImpl() {
//		this.captchaProducer = new DefaultKaptcha();
//	}
//
//	@Override
//	public Map<String, Object> generateCaptcha(String portalName) throws TechnicalException {
//		log.info("Generating CAPTCHA for portal: {}", portalName);
//		Map<String, Object> responseMap = new HashMap<>();
//
//		try {
//			String textColor = PORTAL_COLORS.getOrDefault(portalName, "0,0,0");
//			updateCaptchaConfig(textColor);
//
//			String captchaText = captchaProducer.createText();
//			BufferedImage captchaImage = captchaProducer.createImage(captchaText);
//
//			String captchaImageBase64;
//			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//				ImageIO.write(captchaImage, "png", outputStream);
//				byte[] imageBytes = outputStream.toByteArray();
//				captchaImageBase64 = Base64.getEncoder().encodeToString(imageBytes);
//			}
//
//			CaptchaDetails details = new CaptchaDetails();
//			details.setActive(Boolean.TRUE);
//			details.setCaptchaText(captchaText);
//			details.setIsVerified(Boolean.FALSE);
//			details.setUuid(UUID.randomUUID().toString().replace("-", ""));
//			capthaDetailsRepository.save(details);
//
//			CaptchaResponseDTO captchaResponseDTO = new CaptchaResponseDTO(captchaImageBase64, details.getUuid());
//
//			responseMap.put(Constants.RESPONSE, captchaResponseDTO);
//			responseMap.put(Constants.SUCCESS, "CAPTCHA_GENERATED_SUCCESS");
//
//		} catch (Exception e) {
//			log.error("Error generating CAPTCHA for portal {}: {}", portalName, LogUtil.errorLog(e));
//			throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR,Constants.TECHNICAL_ERROR,
//					"Failed to generate CAPTCHA");
//		}
//
//		return responseMap;
//	}
//
//	private void updateCaptchaConfig(String textColor) {
//		Properties properties = new Properties();
//		properties.setProperty("kaptcha.textproducer.char.length", "5");
//		properties.setProperty("kaptcha.textproducer.font.names", "Arial, Courier, Georgia");
//		properties.setProperty("kaptcha.textproducer.font.color", textColor);
//		properties.setProperty("kaptcha.textproducer.font.size", "40");
//		properties.setProperty("kaptcha.textproducer.char.space", "5");
//		properties.setProperty("kaptcha.background.clear.from", "255,255,255");
//		properties.setProperty("kaptcha.background.clear.to", "246,246,247");
//		properties.setProperty("kaptcha.noise.color", "black");
//		properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple");
//		properties.setProperty("kaptcha.image.width", "200");
//		properties.setProperty("kaptcha.image.height", "50");
//		properties.setProperty("kaptcha.border", "no");
//
//		captchaProducer.setConfig(new Config(properties));
//	}
//
//}
