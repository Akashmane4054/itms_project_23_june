package com.itms.product.serviceImpl;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.core.util.Constants;
import com.itms.core.util.LogUtil;
import com.itms.product.domain.EmployeeMaster;
import com.itms.product.dto.EmployeeMasterDTO;
import com.itms.product.repository.EmployeeMasterRepository;
import com.itms.product.repository.RegisterMasterRepository;
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

	private static final String CLASSNAME = LoginServiceImpl.class.getSimpleName();

	@Override
	public Map<String, Object> loginWithLoginId(EmployeeMasterDTO employeeMasterDto)
			throws TechnicalException, BussinessException, ContractException {

		Map<String, Object> responseMap = new HashMap<>();
		log.info(LogUtil.startLog(CLASSNAME));

		try {
			EmployeeMaster employee = employeeMasterRepository.findByEmpId(employeeMasterDto.getEmpId());

			if (employee == null) {
				throw new BussinessException(HttpStatus.UNAUTHORIZED, "Invalid login ID");
			}

			if (!employeeMasterDto.getPassword().equals(employee.getPassword())) {
				throw new BussinessException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
			}

			int loginAttempts = getMaxLoginAttempts(employeeMasterDto.getEmpId());

			if (loginAttempts > 3) {
				if (getLastFailedLoginTime12hrs(employeeMasterDto.getEmpId())) {
					resetLoginCount(employeeMasterDto.getEmpId());
				} else {
					responseMap.put("maxlogin", "true");
					return responseMap;
				}
			}

			if (!isValidUser(employeeMasterDto.getEmpId(), employeeMasterDto.getPassword())) {
				updateLoginCount(loginAttempts + 1, employeeMasterDto.getEmpId());
//				responseMap.put("roleType", " ");
				responseMap.put("redirect", "sessionTimeout");
				return responseMap;
			}

			String forcePassword = "false";
			if (employee.getLastPasswordChange() == null || ChronoUnit.MONTHS
					.between(employee.getLastPasswordChange().toLocalDate(), LocalDate.now()) >= 3) {
				forcePassword = "true";
			}

			if (!"Y".equalsIgnoreCase(employee.getIsMobileVerified())) {
				responseMap.put("loginOtp", "true");
				responseMap.put("popupMessage", "Please verify your mobile number through OTP validation.");
				return responseMap;
			}

			if (isAlreadyLoggedIn(employeeMasterDto.getEmpId())) {
				if (!"true".equalsIgnoreCase(employeeMasterDto.getLogoutOld())) {
					responseMap.put("alreadyLoggedIn", "true");
					return responseMap;
				} else {
					logoutOldSession(employeeMasterDto.getEmpId());
				}
			}

			String token = jwtService.generateToken(employee.getEmpId());

			insertLastLogin(employeeMasterDto.getEmpId());

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

	public boolean isAlreadyLoggedIn(String empId) {
		EmployeeMaster employee = employeeMasterRepository.findByEmpId(empId);
		if (employee != null) {
			return "true".equalsIgnoreCase(employee.getLoggedIn());
		}
		return false;
	}

	@Transactional
	public void logoutOldSession(String empId) {
		employeeMasterRepository.updateLoggedInStatus(empId, "false");
	}

	@Transactional
	public boolean insertLastLogin(String empId) {
		int rowsUpdated = registerMasterRepository.updateLastLogin(empId);
		if (rowsUpdated > 0) {
			log.info("Last login timestamp and status updated for EMPID: {}", empId);
			return true;
		} else {
			log.warn("No register_master record found for EMPID: {}", empId);
			return false;
		}
	}

}
