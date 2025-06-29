package com.itms.product.serviceImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.core.util.Constants;
import com.itms.core.util.LogUtil;
import com.itms.product.domain.EmployeeMaster;
import com.itms.product.dto.EmployeeMasterDto;
import com.itms.product.repository.EmployeeMasterRepository;
import com.itms.product.service.LoginService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private EmployeeMasterRepository employeeMasterRepository;

	private static final String CLASSNAME = LoginServiceImpl.class.getSimpleName();

	@Override
	public Map<String, Object> loginWithLoginId(EmployeeMasterDto employeeMasterDto)
			throws TechnicalException, BussinessException, ContractException {

		Map<String, Object> responseMap = new HashMap<>();

		try {

			EmployeeMaster employee = employeeMasterRepository.findByEmpId(employeeMasterDto.getEmpId());

			if (employee == null) {
				throw new BussinessException(HttpStatus.UNAUTHORIZED, "Invalid login ID");
			}


			// Validate password
			if (!passwordEncoder.matches(employeeMasterDto.getPassword(), employee.getPassword())) {
				throw new BussinessException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
			}

			// Generate JWT token
			String token = jwtTokenUtil.generateToken(employee.getEmpId());

			// Prepare response map
			responseMap.put(Constants.SUCCESS, "User Authenticated Successfully");
			responseMap.put("token", token);
			responseMap.put("employeeId", employee.getEmpId());
			responseMap.put(Constants.ERROR, null);
		} catch (Exception e) {
			log.error(LogUtil.errorLog(e));
			throw new TechnicalException(Constants.TECHNICAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		log.info(LogUtil.exitLog(CLASSNAME));
		return responseMap;
	}

}
