package com.itms.product.serviceImpl;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.core.util.Constants;
import com.itms.core.util.LogUtil;
import com.itms.product.domain.EmployeeMaster;
import com.itms.product.domain.UserToken;
import com.itms.product.dto.EmployeeMasterDTO;
import com.itms.product.filter.CustomUserDetails;
import com.itms.product.repository.EmployeeMasterRepository;
import com.itms.product.repository.RegisterMasterRepository;
import com.itms.product.repository.UserTokenRepository;
import com.itms.product.service.JwtService;
import com.itms.product.service.LoginService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private EmployeeMasterRepository employeeMasterRepository;

	@Autowired
	private RegisterMasterRepository registerMasterRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserTokenRepository userTokenRepository;

	private static final String CLASSNAME = LoginServiceImpl.class.getSimpleName();

	@Override
	@Transactional
	public Map<String, Object> loginWithLoginId(EmployeeMasterDTO employeeMasterDto)
			throws TechnicalException, BussinessException, ContractException {

		Map<String, Object> responseMap = new HashMap<>();
		log.info(LogUtil.startLog(CLASSNAME));

		try {

			// 1️ Check user existence
			EmployeeMaster employee = employeeMasterRepository.findByEmpId(employeeMasterDto.getEmpId());
			if (employee == null) {
				throw new BussinessException(HttpStatus.UNAUTHORIZED, "Invalid login ID");
			}

			// 3️ Check login attempt count
			int loginAttempts = getMaxLoginAttempts(employeeMasterDto.getEmpId());
			if (loginAttempts > 3) {
				if (getLastFailedLoginTime12hrs(employeeMasterDto.getEmpId())) {
					resetLoginCount(employeeMasterDto.getEmpId());
				} else {
					responseMap.put("maxlogin", "true");
					return responseMap;
				}
			}

			// 4️ Validate user against DB
			if (!isValidUser(employeeMasterDto.getEmpId(), employeeMasterDto.getPassword())) {
				updateLoginCount(loginAttempts + 1, employeeMasterDto.getEmpId());
//				responseMap.put("roleType", " ");
//				responseMap.put("redirect", "sessionTimeout");
//				return responseMap;

				throw new BussinessException(HttpStatus.UNAUTHORIZED, "Invalid credentials");

			}

			// 5️ Check password age (3 months policy)
			String forcePassword = "false";
			if (employee.getLastPasswordChange() == null || ChronoUnit.MONTHS
					.between(employee.getLastPasswordChange().toLocalDate(), LocalDate.now()) >= 3) {

				responseMap.put("forcePasswordChange", "true");
				return responseMap;
			}

			// 6️. Check mobile number verification
			if (!"Y".equalsIgnoreCase(employee.getIsMobileVerified())) {
				responseMap.put("loginOtp", "true");
				responseMap.put("popupMessage", "Please verify your mobile number through OTP validation.");
				return responseMap;
			}

			List<UserToken> activeTokens = userTokenRepository.findByEmpIdAndLoggedInTrue(employeeMasterDto.getEmpId());

			if (!activeTokens.isEmpty()) {
				if (!"true".equalsIgnoreCase(employeeMasterDto.getLogoutOld())) {
					responseMap.put(Constants.IS_ALREADY_LOGIN, Boolean.TRUE);
					return responseMap;
				} else {
					// LogoutOld = true — revoke (delete) all active tokens
					for (UserToken token : activeTokens) {
						userTokenRepository.deleteByToken(token.getToken());
					}
				}
			}

			// 8️. Generate JWT token
			String token = jwtService.generateToken(employee.getEmpId());

			// 9️ Save new UserToken record
			UserToken userToken = new UserToken();
			userToken.setEmpId(employee.getEmpId());
			userToken.setToken(token);
			userToken.setExpiration(jwtService.getExpirationDateFromToken(token));

			userToken.setLoggedIn(true);
			userTokenRepository.save(userToken);

			responseMap.put("success", "User Authenticated Successfully");
			responseMap.put("token", token);
			responseMap.put("employeeId", employee.getEmpId());
			responseMap.put("mobileVerified", employee.getIsMobileVerified());
//			responseMap.put("forcePassword", forcePassword);
			responseMap.put("roleType", "Employee");
			responseMap.put("error", null);

		} catch (BussinessException | ContractException e) {
			log.error(LogUtil.errorLog(e));
			throw e;
		} catch (Exception e) {
			log.error(LogUtil.errorLog(e));
			throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.TECHNICAL_ERROR);
		}

		log.info(LogUtil.exitLog(CLASSNAME));
		return responseMap;
	}

	public int getMaxLoginAttempts(String empId) {
		Integer count = employeeMasterRepository.findMaxLoginCountByEmpId(empId);
		return count != null ? count : 0;
	}

	public boolean getLastFailedLoginTime12hrs(String empId) {
		Timestamp lastFailedLoginTime = employeeMasterRepository.getLastFailedLoginTime(empId);

		if (lastFailedLoginTime != null) {
			LocalDateTime failedTime = lastFailedLoginTime.toLocalDateTime();
			LocalDateTime currentTime = LocalDateTime.now();

			Duration duration = Duration.between(failedTime, currentTime);

			long diffHours = duration.toHours();

			log.info("Login time difference for {} is {} hours", empId, diffHours);

			return diffHours > 12;
		}

		return false;
	}

	@Transactional
	public void resetLoginCount(String empId) {
		int rowsUpdated = employeeMasterRepository.resetLoginCount(empId);
		if (rowsUpdated > 0) {
			log.info("Reset login count successfully for {}", empId);
		} else {
			log.warn("No records found for EMP_ID: {}", empId);
		}

	}

	@Transactional
	public void updateLoginCount(int count, String empId) {
		Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());

		int rowsUpdated = employeeMasterRepository.updateLoginCountAndFailedTime(count, currentTime, empId);
		if (rowsUpdated > 0) {
			log.info("Updated login count and last failed time for {}", empId);
		} else {
			log.warn("No records found for EMP_ID: {}", empId);
		}
	}

	public boolean isValidUser(String empId, String password) {
		int userCount = employeeMasterRepository.countValidUser(empId, password);
		log.info("Login attempt for EMP_ID {} - Valid User: {}", empId, userCount > 0);
		return userCount > 0;
	}

//	public boolean isAlreadyLoggedIn(String empId) {
//		UserToken userToken = userTokenRepository.findTokensByEmpIdAndLoggedIn(empId, Boolean.TRUE);
//		if (userToken != null) {
//			return Boolean.TRUE.equals(userToken.getLoggedIn());
//		}
//		return false;
//	}

	@Transactional
	public void logoutOldSession(String empId) {
		userTokenRepository.updateLoggedInStatus(empId, Boolean.FALSE);
	}

	private void setIsAlreadyLoginFlagAndRevokeIfFirstTime(Map<String, Object> map, List<UserToken> tokens,
			EmployeeMasterDTO employeeMasterDto) {

		String logoutOld = employeeMasterDto.getLogoutOld();

		for (UserToken token : tokens) {
			if (token.getExpiration().after(new Date())) {
				// Active token found — mark already logged-in
				map.put(Constants.IS_ALREADY_LOGIN, Boolean.TRUE);

				// If first-time login and old token exists — revoke it
				if (logoutOld.equalsIgnoreCase("true")) {
					userTokenRepository.deleteByToken(token.getToken());
				} else {
					// If not first-time login, break early since session already exists
					break;
				}
			}
		}
	}

	@Override
	public Map<String, Object> forgatePassword(EmployeeMasterDTO employeeMasterDto)
			throws BussinessException, TechnicalException, ContractException {

		Map<String, Object> responseMap = new HashMap<>();
		log.info(LogUtil.startLog(CLASSNAME));

		try {

		} catch (Exception e) {
			log.error(LogUtil.errorLog(e));
			throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.TECHNICAL_ERROR);
		}

		log.info(LogUtil.exitLog(CLASSNAME));
		return responseMap;
	}

	private String fetchAndSetLoggedInUserIdAndLoginId(Map<String, Object> oauthReturnMap, String loggedInEmpId)
			throws TechnicalException, BussinessException, ContractException {
		EmployeeMaster employeeMaster = getLoggedInUserDTO();
		if (employeeMaster != null) {
			loggedInEmpId = employeeMaster.getEmpId();
			oauthReturnMap.put("empId", loggedInEmpId);
		}
		return loggedInEmpId;
	}

	@Override
	public Map<String, Object> getLoggedInUser(MultiValueMap<String, String> headers)
			throws BussinessException, TechnicalException, ContractException {

		log.info(LogUtil.startLog(CLASSNAME));

		Map<String, Object> responseMap = new HashMap<>();
		Map<String, Object> oauthReturnMap = new HashMap<>();
		String loggedInEmpId = null;

		try {
			String token = extractBearerToken(headers);
			validateToken(token);

			loggedInEmpId = fetchAndSetLoggedInUserIdAndLoginId(oauthReturnMap, loggedInEmpId);

			validateUserIdFromToken(oauthReturnMap, loggedInEmpId);

			log.info("OAuth Map: {}", oauthReturnMap);
			log.info("Logged in Employee ID: {}", loggedInEmpId);

			EmployeeMaster employeeMaster = employeeMasterRepository.findByEmpId(loggedInEmpId);

			if (employeeMaster != null) {

				responseMap.put("empId", employeeMaster.getEmpId());
				responseMap.put("empName", employeeMaster.getEmpName());
				responseMap.put("email", employeeMaster.getEmailId());

			} else {
				throw new BussinessException(HttpStatus.UNAUTHORIZED, "Employee not found for given ID.");
			}

		} catch (BussinessException | ContractException e) {
			throw e;
		} catch (Exception e) {
			log.error(LogUtil.errorLog(e));
			throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.TECHNICAL_ERROR);
		}

		log.info(LogUtil.exitLog(CLASSNAME));
		return responseMap;
	}

	private String extractBearerToken(MultiValueMap<String, String> headers) {

		if (headers == null || !headers.containsKey(Constants.AUTHORIZATION)) {
			throw new IllegalArgumentException("Authorization header is missing.");
		}

		List<String> authHeaders = headers.get(Constants.AUTHORIZATION);
		if (authHeaders == null || authHeaders.isEmpty()) {
			throw new IllegalArgumentException("Authorization header is empty.");
		}

		String authHeader = authHeaders.get(0);
		if (!authHeader.startsWith("Bearer ")) {
			throw new IllegalArgumentException("Invalid Authorization header format.");
		}

		return authHeader.substring(7);
	}

	private void validateToken(String token) throws BussinessException {
		if (jwtService.isTokenBlacklisted(token)) {
			throw new BussinessException(HttpStatus.UNAUTHORIZED, "Invalid access token: Token is blacklisted");
		}
	}

	private void validateUserIdFromToken(Map<String, Object> oauthReturnMap, String loggedInEmpId)
			throws BussinessException {
		if (MapUtils.isEmpty(oauthReturnMap) || !oauthReturnMap.containsKey(Constants.ID)) {
			throw new BussinessException(HttpStatus.UNAUTHORIZED, "Invalid access token: ID not found");
		}

		Object idObject = oauthReturnMap.get(Constants.ID);
		if (idObject == null) {
			throw new BussinessException(HttpStatus.UNAUTHORIZED, "Invalid access token: ID is null");
		}

		String idStr = idObject.toString().trim();
		if (idStr.isEmpty() || "null".equalsIgnoreCase(idStr)) {
			throw new BussinessException(HttpStatus.UNAUTHORIZED, "Invalid access token: ID is empty or 'null'");
		}

		try {
			Long.parseLong(idStr);
		} catch (NumberFormatException e) {
			throw new BussinessException(HttpStatus.UNAUTHORIZED, "Invalid access token: Unable to parse ID");
		}
	}

	@Override
	public EmployeeMasterDTO castToUDTO(EmployeeMaster employeeMaster) {
		log.info(LogUtil.startLog(CLASSNAME));
		EmployeeMasterDTO employeeMasterDTO = new EmployeeMasterDTO();
		employeeMasterDTO.setEmpId(employeeMaster.getEmpId());
//		employeeMasterDTO.setLoginId(employeeMaster.getLoginId());
		return employeeMasterDTO;
	}

	@Override
	public EmployeeMaster getLoggedInUserDTO() throws ContractException, TechnicalException, BussinessException {
		log.info(LogUtil.startLog(CLASSNAME));
		EmployeeMaster employee = null;
		EmployeeMaster defaultEmployee = new EmployeeMaster();

		try {
			UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder
					.getContext().getAuthentication();

			Object principal = authentication.getPrincipal();

			String empId = ((CustomUserDetails) principal).getUsername();

			// Validate token using empId and loggedIn=true
			Optional<UserToken> optionalToken = userTokenRepository.findByEmpIdAndLoggedIn(empId, Boolean.TRUE);

			if (optionalToken.isEmpty()) {
				throw new BussinessException(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED, "User not authenticated");
			}

			if (empId != null) {
				employee = employeeMasterRepository.findByEmpId(empId);
			}

		} catch (Exception e) {
			log.error("Error retrieving logged-in employee from Security Context", e);
		}

		if (employee == null && defaultEmployee != null) {
			employee = defaultEmployee;
		}

		if (employee != null && employee.getEmpId() != null) {
			log.info("Employee EmpId: " + employee.getEmpId());
		}

		log.info(LogUtil.exitLog(CLASSNAME));
		return employee;
	}

}
