package com.itms.product.serviceImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.itms.product.repository.CapthaDetailsRepository;
import com.itms.product.service.CapchaService;

public class CapchaServiceImpl implements CapchaService {

	@Autowired
	private CapthaDetailsRepository capthaDetailsRepository;

	private static final Map<String, String> PORTAL_COLORS = new HashMap<>();

	static {
		PORTAL_COLORS.put("LIFEMART_SUPER_ADMIN", "216,30,33"); // #d81e21 (Red)
		PORTAL_COLORS.put("LIFEMART_COMPANY", "216,30,33"); // #d81e21 (Red)
		PORTAL_COLORS.put("RESPONDENT_PORTAL", "0,203,68"); // #00cb44 (Green)
		PORTAL_COLORS.put("COMPANY_PORTAL", "255,129,0"); // #ff8100 (Orange)
		PORTAL_COLORS.put("SUPER_ADMIN", "255,129,0"); // #ff8100 (Orange)
	}

//	@Override
//	public Map<String, Object> generateCaptcha(String portalName) throws TechnicalException {
//		log.info("Generating CAPTCHA for portal: {}", portalName);
//		Map<String, Object> map = new HashMap<>();
//
//		String textColor = PORTAL_COLORS.getOrDefault(portalName, "0,0,0");
//
//		Properties properties = new Properties();
//		properties.setProperty("kaptcha.textproducer.char.length", "5");
//		properties.setProperty("kaptcha.textproducer.font.names", "Arial, Courier, Georgia");
//		properties.setProperty("kaptcha.textproducer.font.color", textColor);
//		properties.setProperty("kaptcha.textproducer.font.size", "40");
//		properties.setProperty("kaptcha.textproducer.char.space", "5");
//		properties.setProperty("kaptcha.background.clear.from", "255,255,255"); // White
//		properties.setProperty("kaptcha.background.clear.to", "246,246,247"); // F6F6F7
//		properties.setProperty("kaptcha.noise.color", "black");
//		properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple");
//		properties.setProperty("kaptcha.image.width", "200");
//		properties.setProperty("kaptcha.image.height", "50");
//		properties.setProperty("kaptcha.border", "no");
//
//		captchaProducer.setConfig(new Config(properties));
//
//		String captchaText = captchaProducer.createText();
//
//		BufferedImage captchaImage = captchaProducer.createImage(captchaText);
//
//		String captchaImageBase64;
//		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//			ImageIO.write(captchaImage, "png", outputStream);
//			byte[] imageBytes = outputStream.toByteArray();
//			captchaImageBase64 = Base64.getEncoder().encodeToString(imageBytes);
//
//			CaptchaDetails details = new CaptchaDetails();
//			details.setActive(Boolean.TRUE);
//			details.setCaptchaText(captchaText);
//			details.setIsVerified(Boolean.FALSE);
//			details.setUuid(UUID.randomUUID().toString().replace("-", ""));
//			details = capthaDetailsRepository.save(details);
//
//			CaptchaResponseDTO captchaResponseDTO = new CaptchaResponseDTO(captchaText, captchaImageBase64,
//					details.getUuid());
//
//			map.put("captchaResponse", captchaResponseDTO);
//			map.put(Constants.SUCCESS, "Captcha Generated Successfully");
//			map.put(Constants.ERROR, null);
//		} catch (Exception e) {
//			log.error("Error generating CAPTCHA for portal {}: {}", portalName, LogUtil.errorLog(e));
//			throw new TechnicalException(Constants.TECHNICAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
//		}
//		return map;
//	}

}
