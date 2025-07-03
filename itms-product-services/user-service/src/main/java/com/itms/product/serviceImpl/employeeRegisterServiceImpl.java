package com.itms.product.serviceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.core.util.Constants;
import com.itms.core.util.LogUtil;
import com.itms.product.domain.EmployeeMaster;
import com.itms.product.domain.RegisterMaster;
import com.itms.product.domain.StateMaster;
import com.itms.product.domain.TeamMaster;
import com.itms.product.domain.UserActionHistory;
import com.itms.product.dto.EmployeeMasterDTO;
import com.itms.product.dto.RegisterMasterDTO;
import com.itms.product.repository.EmployeeMasterRepository;
import com.itms.product.repository.RegisterMasterRepository;
import com.itms.product.repository.RoleMasterRepository;
import com.itms.product.repository.StateMasterRepository;
import com.itms.product.repository.TeamMasterRepository;
import com.itms.product.repository.UserActionHistoryRepository;
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

	@Autowired
	private RoleMasterRepository roleMasterRepository;

	@Autowired
	private UserActionHistoryRepository userActionHistoryRepository;

	@Autowired
	private StateMasterRepository stateMasterRepository;

	@Autowired
	private RegisterMasterRepository registerMasterRepository;

	@Override
	@Transactional
	public Map<String, Object> addUser(EmployeeMasterDTO employeeMasterDto)
			throws BussinessException, TechnicalException, ContractException {

		log.info(LogUtil.startLog(CLASSNAME));
		log.info("Adding user: {}", employeeMasterDto.getEmpId());

		Map<String, Object> responseMap = new HashMap<>();

		try {

			String empId = employeeMasterDto.getEmpId();
			String teamName = employeeMasterDto.getEmployeeOf();
			String roleIdStr = employeeMasterDto.getRoleId().toString();
			String tlId = employeeMasterDto.getPredecessor();

			// Check if user already exists
			if (checkUser(empId)) {
				responseMap.put(Constants.SUCCESS, "User already exists");
				responseMap.put(Constants.ERROR, null);
				return responseMap;
			}

			// Get Role Tag
			String userRoleTag = roleMasterRepository.findRoleTagByRoleId(employeeMasterDto.getRoleId());

			// Get Module Code for team
			String moduleCode = String.valueOf(moduleCode(teamName));

			Optional<EmployeeMaster> tlEmployeeOptional = employeeMasterRepository.findById(tlId);

			// Initialize NABARD status as "NNB" by default
			String isNabardType = "NNB";

			// If TL employee record exists
			if (tlEmployeeOptional.isPresent()) {
				EmployeeMaster tlEmployee = tlEmployeeOptional.get();

				// Check if IS_NABARD field is not null, then set "NB", else keep "NNB"
				if (tlEmployee.getIsNabard() != null) {
					isNabardType = "NB";
				}
			}

			// Add User 14 = spoc member

			if (roleIdStr.equals("14")) {
				addSpoc(teamName, empId, moduleCode, roleIdStr, tlId, isNabardType);
			} else {
				addUser(teamName, empId, moduleCode, roleIdStr);
			}

			// Add User History
			addUserHistory(teamName, empId, moduleCode, userRoleTag, tlId);

			responseMap.put(Constants.SUCCESS, "User Added Successfully");
			responseMap.put("employeeId : ", empId);
			responseMap.put(Constants.ERROR, null);

		} catch (Exception e) {
			log.error(LogUtil.errorLog(e));
			throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.TECHNICAL_ERROR);
		}

		log.info(LogUtil.exitLog(CLASSNAME));
		return responseMap;
	}

	public boolean checkUser(String empId) {
		log.info("Checking if user exists with EMP_ID: {}", empId);
		long count = employeeMasterRepository.countByEmpId(empId);
		return count > 0;
	}

	public Long moduleCode(String teamName) throws BussinessException {
		log.info("Fetching MODULE_CODE for team/module: {}", teamName);
		return teamMasterRepository.findByModuleName(teamName).map(TeamMaster::getModuleCode)
				.orElseThrow(() -> new BussinessException(HttpStatus.NOT_FOUND, "Module not found for: " + teamName));
	}

	public boolean addSpoc(String teamName, String userId, String moduleCode, String roleId, String tlId, String var) {
		log.info("Adding SPOC user: {}", userId);

		EmployeeMaster spoc = new EmployeeMaster();
		spoc.setEmpId(userId);
		spoc.setEmployeeOf(teamName);
		spoc.setModuleCode(Integer.parseInt(moduleCode));
		spoc.setRoleId(Integer.parseInt(roleId));
		spoc.setEmpName("NA");
		spoc.setEmpStatus(1);
		spoc.setPredecessor(tlId);

		employeeMasterRepository.save(spoc);
		log.info("SPOC user successfully added: {}", userId);
		return true;
	}

	public boolean addUser(String teamName, String empId, String moduleCode, String roleId) {
		log.info("Adding User: {}", empId);

		EmployeeMaster user = new EmployeeMaster();
		user.setEmpId(empId);
		user.setEmployeeOf(teamName);
		user.setModuleCode(Integer.parseInt(moduleCode));
		user.setRoleId(Integer.parseInt(roleId));
		user.setEmpName("NA");
		user.setEmpStatus(1);

		employeeMasterRepository.save(user);
		log.info("User successfully added: {}", empId);
		return true;
	}

	@Transactional
	public void addUserHistory(String teamName, String userId, String moduleCode, String roleTag, String tlId) {
		log.info("Adding user history for EMP_ID: {}", userId);

		UserActionHistory history = new UserActionHistory();
		history.setActionId(UUID.randomUUID().toString().replace("-", ""));
		history.setEmpId(userId);
		history.setEmployeeOf(teamName);
		history.setModuleCode(moduleCode);
		history.setRoleId(roleTag);
		history.setEmpTl(tlId);
		history.setActionDate(LocalDateTime.now());
		history.setAction("USER ADDED");

		userActionHistoryRepository.save(history);
		log.info("User history successfully added for EMP_ID: {}", userId);
	}

	@Override
	@Transactional
	public Map<String, Object> registerUser(RegisterMasterDTO dto)
			throws BussinessException, TechnicalException, ContractException {

		log.info("Registering user: {}", dto.getEmpId());

		Map<String, Object> responseMap = new HashMap<>();

		try {
			
//			// Decrypt password from encrypted input
//			String decryptedPassword = IRManager.decrypt(dto.getPassword()); // assuming you have a password field
//			String encryptedPassword = PasswordCryptography.encrypt(decryptedPassword);
//
//			// Validate password policy
//			boolean isValidPassword = IRManager.validate(decryptedPassword);
//			if (!isValidPassword) {
//				responseMap.put(Constants.SUCCESS, false);
//				responseMap.put(Constants.ERROR, "Password does not match policy");
//				return responseMap;
//			}

			// Get State Code
			Long stateCode = getStateCodeByName(dto.getState());

			// Get Module Code for team
			Long moduleCode = getModuleCode(dto.getTeam());

			// Combine Bank names into comma-separated string
			String bankList = dto.getBank(); // assuming this is already comma-separated in DTO, or process as needed

			// Save data into REGISTER_MASTER table via entity & repository
			RegisterMaster register = new RegisterMaster();
			register.setEmpId(dto.getEmpId());
			register.setUName(dto.getUName());
			register.setGender(dto.getGender());
			register.setEmail(dto.getEmail());
			register.setEmail2(dto.getEmail2());
			register.setBDay(dto.getBDay());
			register.setBMonth(dto.getBMonth());
			register.setBYear(dto.getBYear());
			register.setJDay(dto.getJDay());
			register.setJMonth(dto.getJMonth());
			register.setJYear(dto.getJYear());
			register.setMobile(dto.getMobile());
			register.setExtension(dto.getExtension());
			register.setBlood(dto.getBlood());
			register.setTeam(dto.getTeam());
			register.setCity(dto.getCity());
			register.setState(dto.getState());
			register.setDesignation(dto.getDesignation());
			register.setProject(dto.getProject());
			register.setBank(bankList);
			register.setAlternateNo(dto.getAlternateNo());
			register.setLastLogin(dto.getLastLogin());
			register.setDob(dto.getDob());
			register.setDoj(dto.getDoj());
			register.setNotification(dto.getNotification());
			register.setLoggedIn(dto.getLoggedIn());
			register.setSessionId(dto.getSessionId());
			register.setAddress(dto.getAddress());
			register.setContractDate(dto.getContractDate());
//			register.setEnPassword(encryptedPassword);

			registerMasterRepository.save(register);

			// Log registration history
			registerUserHistory(dto.getEmpId(), dto.getTeam(), dto.getDesignation());

			// If role is TL, update team owner
			if ("TL".equalsIgnoreCase(dto.getDesignation())) {
				updateTeamOwner(dto.getEmpId(), dto.getUName(), moduleCode.toString());
			}

			// 9️⃣ Prepare response
			responseMap.put(Constants.SUCCESS, "User Registered Successfully!");
			responseMap.put(Constants.ERROR, null);

		} catch (Exception e) {
			log.error("Error registering user: {}", LogUtil.errorLog(e));
			throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.TECHNICAL_ERROR);
		}

		return responseMap;
	}

	public Long getStateCodeByName(String stateName) {
		return stateMasterRepository.findByStateName(stateName).map(StateMaster::getStateCode)
				.orElseThrow(() -> new BussinessException(HttpStatus.NOT_FOUND, "State not found for: " + stateName));
	}

	public Long getModuleCode(String moduleName) {
		log.info("Fetching module code for module/team name: {}", moduleName);

		return teamMasterRepository.findByModuleName(moduleName).map(TeamMaster::getModuleCode)
				.orElseThrow(() -> new BussinessException(HttpStatus.NOT_FOUND, "Module not found for: " + moduleName));
	}

	@Transactional
	public boolean registerUserHistory(String userId, String teamName, String roleName) {
		log.info("Adding register user history for EMP_ID: {}", userId);

		try {
			UserActionHistory history = new UserActionHistory();
			history.setActionId(UUID.randomUUID().toString().replace("-", ""));
			history.setEmpId(userId);
			history.setEmployeeOf(teamName);
			history.setRoleId(roleName);
			history.setActionDate(LocalDateTime.now());
			history.setAction("USER REGISTERED");

			userActionHistoryRepository.save(history);

			log.info("User registration history added for EMP_ID: {}", userId);
			return true;

		} catch (Exception e) {
			log.error("Error while adding register user history: {}", e.getMessage());
			throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.TECHNICAL_ERROR);
		}
	}

	public boolean updateTeamOwner(String userId, String empName, String moduleCode) {
		log.info("Updating Team Owner for module: {}", moduleCode);

		try {
			int updatedRows = teamMasterRepository.updateTeamOwner(userId, empName, Long.valueOf(moduleCode));

			if (updatedRows > 0) {
				log.info("Team owner successfully updated for module: {}", moduleCode);
				return true;
			} else {
				log.warn("No records updated for module: {}", moduleCode);
				return false;
			}

		} catch (Exception e) {
			log.error("Error while updating team owner: {}", e.getMessage());
			throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.TECHNICAL_ERROR);
		}
	}

}
