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
import com.itms.product.domain.TeamMaster;
import com.itms.product.dto.EmployeeMasterDTO;
import com.itms.product.repository.EmployeeMasterRepository;
import com.itms.product.repository.TeamMasterRepository;
import com.itms.product.service.EmployeeRegisterService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class employeeRegisterServiceImpl implements EmployeeRegisterService {

	private static final String CLASSNAME = LoginServiceImpl.class.getSimpleName();

	@Autowired
	private EmployeeMasterRepository employeeMasterRepository;

	@Autowired
	private TeamMasterRepository teamMasterRepository;

	@Override
	@Transactional
	public Map<String, Object> addUser(EmployeeMasterDTO employeeMasterDto)
			throws BussinessException, TechnicalException, ContractException {

		Map<String, Object> responseMap = new HashMap<>();
		log.info(LogUtil.startLog(CLASSNAME));
		log.info("Adding user: {}", employeeMasterDto.getEmpId());

		try {

			String empId = employeeMasterDto.getEmpId();
			String teamName = employeeMasterDto.getEmployeeOf();
			String roleName = employeeMasterDto.getRoleId().toString();
			String tlId = employeeMasterDto.getPredecessor(); // Assuming 'predecessor' is acting as 'team lead id'

			// Check if user already exists
			if (checkUser(empId)) {
				responseMap.put(Constants.SUCCESS, "User already exists");
				responseMap.put(Constants.ERROR, null);
				return responseMap;
			}

			// Fetch Role Tag
			String userRoleTag = roleMasterRepository.findById(employeeMasterDto.getRoleId())
					.map(role -> role.getRoleTag()).orElse(null);

			// Get Module Code for team
			String moduleCode = String.valueOf(ModuleCode(teamName));

			// Fetch team lead's IS_NABARD status
			String isNabardType = employeeMasterRepository.findById(tlId)
					.map(emp -> emp.getIsNabard() != null ? "NB" : "NNB").orElse("NNB");

			// Add user based on role
			if (roleName.equals("14")) {
				addSpoc(teamName, empId, moduleCode, roleName, tlId, isNabardType);
			} else {
				addUser(teamName, empId, moduleCode, roleName);
			}

			// Add User History
			adminManagerService.addUserHistory(teamName, empId, moduleCode, userRoleTag, tlId);

			responseMap.put(Constants.SUCCESS, "User Added Successfully");
			responseMap.put("employeeId ", employeeMasterDto.getEmpId());
			responseMap.put(Constants.ERROR, null);

		} catch (Exception e) {
			log.error(LogUtil.errorLog(e));
			throw new TechnicalException(Constants.TECHNICAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		log.info(LogUtil.exitLog(CLASSNAME));
		return responseMap;
	}

	public boolean checkUser(String empId) {
		log.info("Checking if user exists with EMP_ID: {}", empId);

		try {
			long empCount = employeeMasterRepository.countByEmpId(empId);
			return empCount > 0;
		} catch (Exception e) {
			log.error("Error while checking user existence: {}", LogUtil.errorLog(e));
			throw new TechnicalException(Constants.TECHNICAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public int ModuleCode(String assignTo) throws BussinessException {
		log.info("Fetching MODULE_CODE for team/module: {}", assignTo);

		try {
			return teamMasterRepository.findByModuleName(assignTo).map(TeamMaster::getModuleCode)
					.orElseThrow(() -> new BussinessException("Module not found for: " + assignTo));
		} catch (Exception e) {
			log.error("Error while fetching module code: {}", LogUtil.errorLog(e));
			throw new TechnicalException(Constants.TECHNICAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public boolean addSpoc(String teamName, String userId, String moduleCode, String roleName, String tlId,
			String var) {
		log.info("Adding SPOC user: {}", userId);

		try {
			EmployeeMaster spoc = new EmployeeMaster();
			spoc.setEmpId(userId);
			spoc.setEmployeeOf(teamName);
			spoc.setModuleCode(Integer.parseInt(moduleCode));
			spoc.setRoleId(Integer.parseInt(roleName));
			spoc.setEmpName("NA");
			spoc.setEmpStatus(1);
			spoc.setPredecessor(tlId);

			employeeMasterRepository.save(spoc);

			log.info("SPOC user successfully added: {}", userId);
			return true;

		} catch (Exception e) {
			log.error("Error adding SPOC user: {}", LogUtil.errorLog(e));
			throw new TechnicalException(Constants.TECHNICAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public boolean addUser(String teamName, String empId, String moduleCode, String roleName) {
		log.info("Adding User: {}", empId);

		try {
			EmployeeMaster user = new EmployeeMaster();
			user.setEmpId(empId);
			user.setEmployeeOf(teamName);
			user.setModuleCode(Integer.parseInt(moduleCode));
			user.setRoleId(Integer.parseInt(roleName));
			user.setEmpName("NA");
			user.setEmpStatus(1);

			employeeMasterRepository.save(user);

			log.info("User successfully added: {}", empId);
			return true;

		} catch (Exception e) {
			log.error("Error while adding user: {}", LogUtil.errorLog(e));
			throw new TechnicalException(Constants.TECHNICAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
