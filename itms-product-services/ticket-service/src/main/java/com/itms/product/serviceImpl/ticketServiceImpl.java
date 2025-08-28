package com.itms.product.serviceImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.core.util.ExceptionUtil;
import com.itms.core.util.userServiceUtil;
import com.itms.product.domain.BankDetails;
import com.itms.product.domain.CallHistoryMaster;
import com.itms.product.domain.CallMaster;
import com.itms.product.domain.IssueList;
import com.itms.product.domain.PriorityCode;
import com.itms.product.domain.UploadFile;
import com.itms.product.dto.CrTicketDTO;
import com.itms.product.dto.IssueTicketRequestDTO;
import com.itms.product.dto.ServiceTicketDTO;
import com.itms.product.repository.BankDetailsRepository;
import com.itms.product.repository.CallHistoryRepository;
import com.itms.product.repository.CallMasterRepository;
import com.itms.product.repository.IssueListRepository;
import com.itms.product.repository.PriorityCodeRepository;
import com.itms.product.repository.SlaMasterRepository;
import com.itms.product.repository.TicketRuleRepository;
import com.itms.product.repository.UploadFileRepository;
import com.itms.product.service.ticketService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ticketServiceImpl implements ticketService {

	private static final String CLASSNAME = ticketServiceImpl.class.getSimpleName();

	@Autowired
	private CallMasterRepository callMasterRepository;

	@Autowired
	private CallHistoryRepository callHistoryRepository;

	private TicketSequenceServiceImpl ticketSequenceService;

	@Autowired
	private IssueListRepository issueListRepository;

	@Autowired
	private BankDetailsRepository bankDetailsRepository;

	@Autowired
	private userServiceUtil userServiceUtil;

	@Autowired
	private PriorityCodeRepository priorityCodeRepository;

	@Autowired
	private UploadFileRepository uploadFileRepository;

	@Autowired
	private SlaMasterRepository slaMasterRepository;

	@Autowired
	private TicketRuleRepository ticketRuleRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Map<String, Object> issueTicket(IssueTicketRequestDTO dto, MultiValueMap<String, Object> headers)
			throws BussinessException, TechnicalException, ContractException {

		log.info("Ticket Creation: START");
		Map<String, Object> responseMap = new HashMap<>();

		Map<String, Object> userResponse = verifyToken(headers);

		try {
			// Step 1: Get bank details
			BankDetails bankDetails = bankDetailsRepository.findByBankName(dto.getBankname());

			// Step 2: Generate Unique Ticket ID
//			String ticketId = ticketSequenceService.generateTicketNumber(bankDetails.getBankGroup(), "T");

			String ticketId = generateTicketNumber(bankDetails.getBankGroup(), "T");

			// Step 3: Get required codes
			int moduleCode = userServiceUtil.getModuleCodeByTeamName(dto.getAssigntoCRE());
			int bankCode = bankDetails.getBankCode();

			PriorityCode priority = priorityCodeRepository.findByPriorityLevel(dto.getPriority());
			int priorityCode = priority.getPriorityCode();

			String vhId = userServiceUtil.findVhIdByModuleCode(moduleCode);

			String inBucketOf = (vhId != null && !vhId.isEmpty()) ? vhId
					: userServiceUtil.findTlIdByModuleCode(moduleCode);

			// Step 4: Upload BRD/Circular Files (if any)
			handleFileUploads(dto, ticketId);

			// Step 5: Evaluate SLA
			String sla = (dto.getSla() == null || dto.getSla().trim().isEmpty())
					? issueListRepository.findSlaByTeamAndIssue(dto.getAssigntoCRE(), dto.getIssueDescription())
					: dto.getSla().trim();

			String notify;
			int callStatus;

			if (sla == null || sla.equalsIgnoreCase("NA")) {
				notify = "Fresh";
				callStatus = 0; // Or whatever default "fresh" status code is
			} else {
				notify = "accept";
				callStatus = 6;
			}

			Integer slaTime = slaMasterRepository.findSlaTimeBySlaId(sla);

			LocalDateTime currentTime = LocalDateTime.now();
			LocalDateTime expiryTime = currentTime.plusHours(slaTime);

			Timestamp expTimeTimestamp = Timestamp.valueOf(expiryTime);

			Optional<String> optionalRuleBkt = ticketRuleRepository.findEmpIdByRule(moduleCode,
					bankDetails.getBankGroup(), dto.getIssueDescription());

			String ruleBkt = optionalRuleBkt.orElseGet(() -> ticketRuleRepository
					.findEmpIdByOldIssueMapping(moduleCode, bankDetails.getBankGroup(), dto.getIssueDescription())
					.orElse(""));

			log.info("Rule BKT from DB: {}", ruleBkt);

			if (!ruleBkt.isEmpty()) {
				inBucketOf = ruleBkt;

				if (sla == null || "NA".equalsIgnoreCase(sla.trim())) {
					notify = "Fresh";
					callStatus = 0;
				} else {
					notify = "accept";
					callStatus = 6;
				}
			}

			String ticPop = "";

			if ("reviewticket".equalsIgnoreCase(dto.getReviewTicket())) {

				String tlId = userServiceUtil.findTlIdByModuleCode(Integer.parseInt(dto.getUserTeam()));

				if (tlId != null && !tlId.isEmpty()) {
					inBucketOf = tlId;
					log.info("Ticket inBucketOf TL: {}", inBucketOf);
					ticPop = "This Ticket will go to your TL, Please ask TL to forward it to the respective Team.";
				} else {
					log.warn("No TL found for moduleCode: {}", dto.getUserTeam());
				}

			} else {
				log.info("No reviewticket flag set. Current inBucketOf: {}", inBucketOf);
			}

			// SPOC role check
			if (dto.getUserRole() == 14) {
				log.info("Begin fetching SPOC TL for EMP_ID: {}", dto.getUserId());

				String spocTL = userServiceUtil.findSpocTlByEmpId(dto.getUserId());

				if (spocTL != null && !spocTL.isEmpty()) {
					inBucketOf = spocTL;
					log.info("SPOC TL found: {}", spocTL);
				} else {
					log.warn("No SPOC TL found for EMP_ID: {}", dto.getUserId());
				}
			}

			// Banker
			if (dto.getUserRole() == 13) {
				log.info("Begin fetching SPOC TL for EMP_ID: {}", dto.getUserId());

				String spocTL = userServiceUtil.findSpocTlByEmpId(dto.getUserId());

				if (spocTL != null && !spocTL.isEmpty()) {
					inBucketOf = spocTL;
					log.info("SPOC TL found: {}", spocTL);
				} else {
					log.warn("No SPOC TL found for EMP_ID: {}", dto.getUserId());
				}
			}

			// Step 6: Prepare and Save CallMaster
			CallMaster callMaster = buildCallMasterEntity(dto, bankCode, ticketId);
			callMaster.setCallStatusCode(callStatus);
			callMaster.setSlaLevel(sla);
			callMaster.setNotification(notify);
			callMaster.setCallAssignedModuleCode(moduleCode);
			callMaster.setInBucketOf(inBucketOf);

			callMasterRepository.save(callMaster);

			// Step 7: Save CallHistory
			CallHistoryMaster callHistory = buildCallHistoryEntity(dto, ticketId, moduleCode, callStatus, inBucketOf,
					sla, priorityCode);
			callHistoryRepository.save(callHistory);

			responseMap.put("ticketId", ticketId);
			responseMap.put("message", "Ticket created successfully");

		} catch (Exception ex) {
			log.error("Ticket creation failed", ex);
			throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, "Ticket creation failed");
		}

		log.info("Ticket Creation: END");
		return responseMap;
	}

	@Transactional
	private String generateTicketNumber(String bankGroup, String bankType) {
		String ticketSeq = bankGroup + bankType; // e.g. CAC, NBT, RRT etc.

		String sql = "SELECT nxt_" + ticketSeq + "_issue_seq() FROM dual";

		Object result = entityManager.createNativeQuery(sql).getSingleResult();
		return String.valueOf(result);
	}

//	@Transactional
//	private String generateTicketNumber(String bankGroup, String bankType) {
//		String ticketSeq = bankGroup + bankType;
//
//		String query = switch (ticketSeq) {
//		case "RRT" -> "SELECT nxt_RRT_issue_seq.NEXTVAL FROM dual";
//		case "RRS" -> "SELECT nxt_RRS_issue_seq.NEXTVAL FROM dual";
//		case "RRC" -> "SELECT nxt_RRC_issue_seq.NEXTVAL FROM dual";
//		case "RRI" -> "SELECT nxt_RRI_issue_seq.NEXTVAL FROM dual";
//		case "UCT" -> "SELECT nxt_UCT_issue_seq.NEXTVAL FROM dual";
//		case "UCS" -> "SELECT nxt_UCS_issue_seq.NEXTVAL FROM dual";
//		case "UCC" -> "SELECT nxt_UCC_issue_seq.NEXTVAL FROM dual";
//		case "UCI" -> "SELECT nxt_UCI_issue_seq.NEXTVAL FROM dual";
//		case "CBT" -> "SELECT nxt_CBT_issue_seq.NEXTVAL FROM dual";
//		case "CBS" -> "SELECT nxt_CBS_issue_seq.NEXTVAL FROM dual";
//		case "CBC" -> "SELECT nxt_CBC_issue_seq.NEXTVAL FROM dual";
//		case "CBI" -> "SELECT nxt_CBI_issue_seq.NEXTVAL FROM dual";
//		case "NBT" -> "SELECT nxt_NBT_issue_seq.NEXTVAL FROM dual";
//		case "NBS" -> "SELECT nxt_NBS_issue_seq.NEXTVAL FROM dual";
//		case "NBC" -> "SELECT nxt_NBC_issue_seq.NEXTVAL FROM dual";
//		case "NBI" -> "SELECT nxt_NBI_issue_seq.NEXTVAL FROM dual";
//		case "CAT" -> "SELECT nxt_CAT_issue_seq.NEXTVAL FROM dual";
//		case "CAS" -> "SELECT nxt_CAS_issue_seq.NEXTVAL FROM dual";
//		case "CAC" -> "SELECT nxt_CAC_issue_seq.NEXTVAL FROM dual";
//		case "CAI" -> "SELECT nxt_CAI_issue_seq.NEXTVAL FROM dual";
//		case "INT" -> "SELECT nxt_INT_issue_seq.NEXTVAL FROM dual";
//		case "INS" -> "SELECT nxt_INS_issue_seq.NEXTVAL FROM dual";
//		case "INC" -> "SELECT nxt_INC_issue_seq.NEXTVAL FROM dual";
//		case "INI" -> "SELECT nxt_INI_issue_seq.NEXTVAL FROM dual";
//		case "TNT" -> "SELECT nxt_TNT_issue_seq.NEXTVAL FROM dual";
//		case "TNS" -> "SELECT nxt_TNS_issue_seq.NEXTVAL FROM dual";
//		case "TNC" -> "SELECT nxt_TNC_issue_seq.NEXTVAL FROM dual";
//		case "TNI" -> "SELECT nxt_TNI_issue_seq.NEXTVAL FROM dual";
//		case "NCT" -> "SELECT nxt_NCT_issue_seq.NEXTVAL FROM dual";
//		case "NCS" -> "SELECT nxt_NCS_issue_seq.NEXTVAL FROM dual";
//		case "NCC" -> "SELECT nxt_NCC_issue_seq.NEXTVAL FROM dual";
//		case "NCI" -> "SELECT nxt_NCI_issue_seq.NEXTVAL FROM dual";
//		case "WBT" -> "SELECT nxt_WBT_issue_seq.NEXTVAL FROM dual";
//		case "WBS" -> "SELECT nxt_WBS_issue_seq.NEXTVAL FROM dual";
//		case "WBC" -> "SELECT nxt_WBC_issue_seq.NEXTVAL FROM dual";
//		case "WBI" -> "SELECT nxt_WBI_issue_seq.NEXTVAL FROM dual";
//		default -> throw new IllegalArgumentException("Invalid ticket sequence: " + ticketSeq);
//		};
//
//		Object result = entityManager.createNativeQuery(query).getSingleResult();
//		return String.valueOf(result);
//	}

	public Long uploadBrd(MultipartFile brdFile, String irNumber) throws IOException {
		UploadFile fileEntity = new UploadFile();
		fileEntity.setFileName(brdFile.getOriginalFilename());
		fileEntity.setFileExtension(getExtension(brdFile.getOriginalFilename()));
		fileEntity.setFileUp(brdFile.getBytes());
		fileEntity.setIrNumber(irNumber);

		UploadFile saved = uploadFileRepository.save(fileEntity);
		return saved.getAttachmentId();
	}

	public Long uploadCircular(MultipartFile circularFile, String irNumber) throws IOException {
		// Step 1: Fetch latest BRD record for this IR (assuming circular is linked to
		// BRD)
		Optional<UploadFile> optionalFile = uploadFileRepository.findAll().stream()
				.filter(f -> irNumber.equals(f.getIrNumber()))
				.sorted(Comparator.comparingLong(UploadFile::getAttachmentId).reversed()).findFirst();

		if (optionalFile.isPresent()) {
			UploadFile file = optionalFile.get();
			file.setFileName(circularFile.getOriginalFilename());
			file.setFileExtension(getExtension(circularFile.getOriginalFilename()));
			file.setFileUp(circularFile.getBytes());

			UploadFile updated = uploadFileRepository.save(file);
			return updated.getAttachmentId();
		} else {
			throw new RuntimeException("BRD file not found for IR Number: " + irNumber);
		}
	}

	private String getExtension(String filename) {
		return filename != null && filename.contains(".") ? filename.substring(filename.lastIndexOf('.') + 1) : "";
	}

	private void handleFileUploads(IssueTicketRequestDTO dto, String ticketId) throws IOException, SQLException {
		MultipartFile brdFile = dto.getBrdBusinessFile();
		MultipartFile circularFile = dto.getCircularUploadFile();

		if (brdFile != null && !brdFile.isEmpty()) {
			uploadBrd(brdFile, ticketId);
		}

		if (circularFile != null && !circularFile.isEmpty()) {
			uploadCircular(circularFile, ticketId);
		}
	}

	private CallMaster buildCallMasterEntity(IssueTicketRequestDTO dto, int bankCode, String ticketId)
			throws SQLException {
		CallMaster callMaster = new CallMaster();

		callMaster.setCallNo(ticketId);
		callMaster.setCallDesc(dto.getIssueDescription());
		callMaster.setCallIssueRemark(dto.getIssueRemark());
		callMaster.setCallBankBranch(dto.getBankname());
		callMaster.setCallBankName(bankCode);
		callMaster.setCallAssignTo(dto.getAssigntoCRE());
		callMaster.setCallPriority(dto.getPriority());
		callMaster.setCallSeverityLevel(dto.getSeverityLevel());
		callMaster.setCallInitiator(dto.getUserId());
//		callMaster.setRequirementType(dto.getRe);

		LocalDateTime now = LocalDateTime.now();
		callMaster.setCallDate(now);
		callMaster.setExpectedTime(now);

		int priorityCode = getPriorityCode(dto.getPriority());
		callMaster.setCallPriorityCode(priorityCode);

//		callMaster.setSlaLevel(dto.getSlaLevel());
		callMaster.setTicketType(dto.getTicketType());
		callMaster.setTracker(dto.getTracker());
		callMaster.setCallRepeatFlag(dto.getRdoNew());
		callMaster.setCloneReference(dto.getCloneRefId());
		callMaster.setChargeable(dto.getChargeable());

		if (dto.getExtension() != null && !dto.getExtension().isBlank()) {
			callMaster.setExtensionNo(Long.parseLong(dto.getExtension()));
		}

		return callMaster;
	}

	private CallHistoryMaster buildCallHistoryEntity(IssueTicketRequestDTO dto, String ticketId, int moduleCode,
			int callStatus, String inBucketOf, String sla, int priorityCode) {
		CallHistoryMaster history = new CallHistoryMaster();

		String parsedSla = (sla != null && sla.length() >= 2) ? sla.substring(0, 2) : "";
		String slaFallbackCheck = "( ";

		String issueDescription = dto.getIssueDescription();
		String teamName = dto.getAssigntoCRE();

		// Fix legacy issue descriptions
		if ("CIFÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€š opening".equals(issueDescription)) {
			issueDescription = issueDescription.replaceAll("ÃƒÆ’Ã¢â‚¬Å¡", "");
		}

		// Fallback to DB if SLA format is invalid
		if (parsedSla.equalsIgnoreCase(slaFallbackCheck)) {
			String resolvedSla = resolveSlaLevel(teamName, issueDescription);
			dto.setSla(resolvedSla); // Update DTO with resolved value
		}

		// Populate history fields
		history.setCallNo(ticketId);
		history.setEmpId(dto.getUserId());
		history.setAssignedDate(LocalDateTime.now());
		history.setCallRemarks(dto.getIssueRemark());
		history.setFromModule(0);
		history.setToModule(moduleCode);
		history.setToMember(inBucketOf);
		history.setPriorityCode(priorityCode);
		history.setStatusCode(callStatus);
		history.setSlaId(sla);
		history.setAttachmentId(null);

		return history;
	}

	public String resolveSlaLevel(String teamName, String issueDescription) {
		return issueListRepository.findByTeamNameAndIssue(teamName, issueDescription).map(IssueList::getSlaLevel)
				.orElse(null);
	}

	public int getPriorityCode(String priorityLevel) {
		PriorityCode priorityCode = priorityCodeRepository.findByPriorityLevel(priorityLevel);
		return (priorityCode != null) ? priorityCode.getPriorityCode() : 0;
	}

	private Map<String, Object> verifyToken(MultiValueMap<String, Object> headers)
			throws BussinessException, ContractException, TechnicalException {
		return ExceptionUtil.throwExceptionsIfPresent(userServiceUtil.tokenVerification(headers));
	}

	@Override
	public Map<String, Object> serviceTicket(ServiceTicketDTO dto, MultiValueMap<String, Object> headers)
			throws BussinessException, TechnicalException, ContractException {

		log.info("Ticket Creation: START");
		Map<String, Object> responseMap = new HashMap<>();

		Map<String, Object> userResponse = verifyToken(headers);

		String usrId = (String) userResponse.get("userId");
		String usrName = (String) userResponse.get("userName");
		String userTeam = (String) userResponse.get("userTeam");
		int userRole = (int) userResponse.get("userRole");
		int stateCode = (int) userResponse.get("stateCode");

		try {
			// Step 1: Get bank details
			BankDetails bankDetails = bankDetailsRepository.findByBankName(dto.getBankname());

			// Step 2: Generate Unique Ticket ID
			String ticketId = generateTicketNumber(bankDetails.getBankGroup(), "S");

			// Step 3: Get required codes
			int moduleCode = userServiceUtil.getModuleCodeByTeamName(dto.getAssigntoCRE());
			int bankCode = bankDetails.getBankCode();

			PriorityCode priority = priorityCodeRepository.findByPriorityLevel(dto.getPriority());
			int priorityCode = priority.getPriorityCode();

			String vhId = userServiceUtil.findVhIdByModuleCode(moduleCode);

			String inBucketOf = (vhId != null && !vhId.isEmpty()) ? vhId
					: userServiceUtil.findTlIdByModuleCode(moduleCode);

//			// Step 4: Upload BRD/Circular Files (if any)
//			handleFileUploads(dto, ticketId);
//			
			// Step 4: File Upload (return attachmentId if needed)
			String attachmentId = handleFileUploads(dto, ticketId);
			if (attachmentId == null)
				attachmentId = "0";

			// Step 5: Evaluate SLA
			String sla = (dto.getSla() == null || dto.getSla().trim().isEmpty())
					? issueListRepository.findSlaByTeamAndIssue(dto.getAssigntoCRE(), dto.getIssueDescription())
					: dto.getSla().trim();

			// 🔹 CloneRefId logic
			String cloneRefId = (dto.getCloneRefId() == null || dto.getCloneRefId().isEmpty()) ? "--"
					: dto.getCloneRefId();
			if (cloneRefId.equals("--") && sla != null && sla.length() >= 2) {
				sla = sla.substring(0, 2);
			}

			// 🔹 CIF issue description cleanup
			String issueDescription = dto.getIssueDescription();
			if ("CIF opening".equalsIgnoreCase(issueDescription)) {
				issueDescription = issueDescription.replaceAll("Â", "");
			}

			// 🔹 SLA Test validation (fallback SLA calc)
			if ("(".equalsIgnoreCase(sla)) {
				try {
					sla = issueListRepository.findSlaByTeamAndIssue(dto.getAssigntoCRE(), issueDescription);
				} catch (Exception ex) {
					log.error("Fallback SLA fetch failed", ex);
				}
			}

			// 🔹 Radio button logic (from JSP)
			String rdoNew = dto.getRdoNew();
			String radioBtn = (rdoNew == null || rdoNew.isEmpty()) ? "NA" : rdoNew;

			String notify = "accept";
			int callStatus = 6;
			Timestamp expTimeTimestamp = null;

			try {
				if (sla == null || sla.isEmpty()) {
					sla = issueListRepository.findSlaByTeamAndIssue(dto.getAssigntoCRE(), issueDescription);

				}

				if ("NA".equalsIgnoreCase(sla)) {

					String flag24hours = issueListRepository.findFlag24hours(dto.getAssigntoCRE(), issueDescription);

					if ("N".equalsIgnoreCase(flag24hours)) {

						String slaTimeForNA = issueListRepository.findSlaTime(dto.getAssigntoCRE(), issueDescription);

						Date newTimelineSla = calculateTimeline(slaTimeForNA, new Date(), flag24hours);
						expTimeTimestamp = new Timestamp(newTimelineSla.getTime());
					} else {

						String slaTimeForNA = issueListRepository.findSlaTime(dto.getAssigntoCRE(), issueDescription);

						int slaMinutes = Integer.parseInt(slaTimeForNA) * 60;
						expTimeTimestamp = Timestamp.valueOf(LocalDateTime.now().plusMinutes(slaMinutes));
					}
				} else {
					Integer slaTime = slaMasterRepository.findSlaTimeBySlaId(sla);
					int slaMinutes = (slaTime != null ? slaTime : 0) * 60;
					expTimeTimestamp = Timestamp.valueOf(LocalDateTime.now().plusMinutes(slaMinutes));
				}
			} catch (Exception e) {
				log.error("SLA calculation failed", e);
				throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, "SLA calculation failed");
			}

			// Step 6: Rule Bucket check
			Optional<String> optionalRuleBkt = ticketRuleRepository.findEmpIdByRule(moduleCode,
					bankDetails.getBankGroup(), issueDescription);
			String ruleBkt = optionalRuleBkt.orElseGet(() -> ticketRuleRepository
					.findEmpIdByOldIssueMapping(moduleCode, bankDetails.getBankGroup(), issueDescription).orElse(""));

			if (!ruleBkt.isEmpty()) {
				inBucketOf = ruleBkt;
			}

			// Step 7: ReviewTicket logic
			if ("reviewticket".equalsIgnoreCase(dto.getReviewTicket())) {
				String tlId = userServiceUtil.findTlIdByModuleCode(Integer.parseInt(userTeam));
				if (tlId != null && !tlId.isEmpty()) {
					inBucketOf = tlId;
				}
			}

			// Step 8: SPOC/Banker logic
			if (userRole == 14 || userRole == 13) {
				String spocTL = userServiceUtil.findSpocTlByEmpId(usrId);
				if (spocTL != null && !spocTL.isEmpty()) {
					inBucketOf = spocTL;
				}
			}

			// Step 9: Direct SPOC assign & Helpdesk routing
			boolean checkSpocDirectAssign = ticketRuleRepository.checkSpocDirectAssign(moduleCode, "Service");
			if ((userRole == 7 || userRole == 16) && checkSpocDirectAssign) {
				inBucketOf = userServiceUtil.findTlIdByModuleCode(moduleCode);
			} else if ((userRole == 7 || userRole == 16) && (stateCode != 24)) {
				inBucketOf = userServiceUtil.findTlIdByModuleCode(33); // Helpdesk
				moduleCode = 33;
			}

			// Step 10: OwnTeam case
			if ("ownTeam".equalsIgnoreCase(dto.getAssigntoCRE())) {
				moduleCode = Integer.parseInt(userTeam);
				inBucketOf = usrId;
				callStatus = 6;
				notify = "accept";
			}

			// Step 11: Save CallMaster
			CallMaster callMaster = buildCallMasterEntity(dto, bankCode, ticketId);
			callMaster.setCallStatusCode(callStatus);
			callMaster.setSlaLevel(sla);
			callMaster.setNotification(notify);
			callMaster.setCallAssignedModuleCode(moduleCode);
			callMaster.setInBucketOf(inBucketOf);
			callMaster.setExpectedTime(expTimeTimestamp);
			callMaster.setAttachmentId(attachmentId);
			callMaster.setRadioBtn(radioBtn); // 🔹 added radio button info

			callMasterRepository.save(callMaster);

			// Step 12: Save CallHistory
			CallHistoryMaster callHistory = buildCallHistoryEntity(dto, ticketId, moduleCode, callStatus, inBucketOf,
					sla, priorityCode);
			callHistory.setAttachmentId(attachmentId);
			callHistory.setRadioBtn(radioBtn); // 🔹 track in history also
			callHistoryRepository.save(callHistory);

			responseMap.put("ticketId", ticketId);
			responseMap.put("message", "Ticket created successfully");

		} catch (Exception ex) {
			log.error("Ticket creation failed", ex);
			throw new TechnicalException(HttpStatus.INTERNAL_SERVER_ERROR, "Ticket creation failed");
		}

		log.info("Ticket Creation: END");
		return responseMap;
	}

	public Date calculateTimeline(String tat, Date acceptDate, String flag24Hours) {
		// Handle half-hour case (0.5 → 1 hour)
		if (Float.parseFloat(tat) == 0.5) {
			tat = "1";
		}

		long tatLong = Long.parseLong(tat);

		// Convert Date → LocalDateTime
		LocalDateTime acceptDateTime = acceptDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		LocalDateTime newTimeline;

		// Case 1: TAT in days (multiples of 8 hrs)
		long newTatDays = tatLong / 8;
		if (newTatDays > 0) {
			newTimeline = excludingSundays(acceptDateTime, newTatDays);
		} else {
			// Case 2: Less than 1 day TAT → adjust with working hours
			int tatHours = Integer.parseInt(tat);
			newTimeline = workingHoursTat(acceptDateTime, tatHours);
		}

		// Case 3: Special 9-hour flag
		if (!tat.equals("1")) {
			newTimeline = getTimeline9Hrs(tat, acceptDateTime, flag24Hours);
		}

		// Convert back LocalDateTime → Date (if needed)
		return Date.from(newTimeline.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * Add given days to start time, skipping Sundays.
	 */
	public LocalDateTime excludingSundays(LocalDateTime startDateTime, long days) {
		LocalDateTime result = startDateTime;

		for (int i = 0; i < days; i++) {
			result = result.plusDays(1);
			if (result.getDayOfWeek() == DayOfWeek.SUNDAY) {
				result = result.plusDays(1);
			}
		}

		return result;
	}

	/**
	 * SLA calculation with 9-hour working rule.
	 */
	public static LocalDateTime getTimeline9Hrs(String TAT, LocalDateTime acceptDate, String flag24Hours) {
		LocalDateTime newTimeLine = null;
		boolean checkSundayAcceptDate = chedkDateIsSunday(acceptDate);
		boolean checkSaturdayAcceptDate = chedkDateIsSaturday(acceptDate);
		boolean checkSaturday = false;
		boolean checkSunday = false;

		LocalTime WORK_START = LocalTime.of(9, 30);
		LocalTime WORK_END = LocalTime.of(18, 30);

		// === Handle if acceptDate falls on Sunday ===
		if (checkSundayAcceptDate && flag24Hours.equals("N")) {
			acceptDate = acceptDate.plusDays(1).with(WORK_START);
		}

		// === Handle if acceptDate falls on Saturday ===
		if (checkSaturdayAcceptDate && flag24Hours.equals("N")) {
			acceptDate = acceptDate.plusDays(2).with(WORK_START); // Jump to Monday 9:30
		}

		// Define EOD and SOD for acceptDate
		LocalDateTime EOD = LocalDateTime.of(acceptDate.toLocalDate(), WORK_END);
		LocalDateTime SOD = LocalDateTime.of(acceptDate.toLocalDate(), WORK_START);

		int minTotal = Integer.parseInt(TAT) * 60;
		long minutes = Duration.between(acceptDate, EOD).toMinutes();

		// === Case 1: acceptDate before SOD (start of day) ===
		if (acceptDate.isBefore(SOD) || acceptDate.equals(SOD)) {
			LocalDateTime cal = SOD;
			int daysAdded = Integer.parseInt(TAT) / 9;
			float hoursAdded = Float.parseFloat(TAT) - (daysAdded * 9);

			if (daysAdded == 1 && hoursAdded == 0) {
				cal = cal.plusHours(9);
			} else if (daysAdded == 1 && hoursAdded > 0) {
				cal = cal.plusDays(1).with(WORK_START);
			} else {
				if ((Integer.parseInt(TAT) % 9 == 0) && (hoursAdded == 0)) {
					cal = addWorkDays(cal, daysAdded - 1, WORK_START);
				} else {
					cal = addWorkDays(cal, daysAdded, WORK_START);
				}
			}
			cal = cal.plusHours((int) hoursAdded);

			// Sunday/Saturday adjustment
			if (chedkDateIsSunday(cal) && flag24Hours.equals("N")) {
				cal = cal.plusDays(1);
			}
			if (chedkDateIsSaturday(cal) && flag24Hours.equals("N")) {
				cal = cal.plusDays(2);
			}

			newTimeLine = cal;
		}

		// === Case 2: Same day completion (TAT=8 and enough time today) ===
		else if (minutes > 480 && TAT.equals("8")) {
			newTimeLine = acceptDate.plusHours(8);
		}

		// === Case 3: Accept date after EOD ===
		else if (acceptDate.isAfter(EOD)) {
			LocalDateTime cal = SOD.plusDays(1); // Next day start
			int daysAdded = Integer.parseInt(TAT) / 9;
			float hoursAdded = Float.parseFloat(TAT) - (daysAdded * 9);

			if (daysAdded == 1 && hoursAdded == 0) {
				cal = cal.plusDays(1).plusHours(9);
			} else if (daysAdded == 1 && hoursAdded > 0) {
				cal = cal.plusDays(2).with(WORK_START);
			} else {
				if ((Integer.parseInt(TAT) % 9 == 0) && (hoursAdded == 0)) {
					cal = addWorkDays(cal, daysAdded - 1, WORK_START);
				} else {
					cal = addWorkDays(cal, daysAdded, WORK_START);
				}
			}
			cal = cal.plusHours((int) hoursAdded);

			// Sunday/Saturday adjustment
			if (chedkDateIsSunday(cal) && flag24Hours.equals("N")) {
				cal = cal.plusDays(1);
			}
			if (chedkDateIsSaturday(cal) && flag24Hours.equals("N")) {
				cal = cal.plusDays(2);
			}

			newTimeLine = cal;
		}

		// === Case 4: General loop-based calculation (minutes consumption) ===
		else {
			LocalDateTime cal = acceptDate;
			int minLeft = minTotal;

			while (minLeft > 0) {
				LocalDateTime dayEOD = LocalDateTime.of(cal.toLocalDate(), WORK_END);
				LocalDateTime daySOD = LocalDateTime.of(cal.toLocalDate(), WORK_START);

				long diff = Duration.between(cal, dayEOD).toMinutes();
				if (diff > 0) {
					if (Integer.parseInt(TAT) < 9 && diff >= minLeft) {
						cal = cal.plusMinutes(minLeft);
						minLeft = 0;
					} else {
						cal = dayEOD.plusHours(15); // Jump to next day 9:30
						minLeft -= diff;
					}
				} else {
					minLeft = 0;
				}

				// Sunday/Saturday adjustment
				if (chedkDateIsSunday(cal) && flag24Hours.equals("N")) {
					cal = cal.plusDays(1);
				}
				if (chedkDateIsSaturday(cal) && flag24Hours.equals("N")) {
					cal = cal.plusDays(2);
				}

				newTimeLine = cal;
			}
		}

		return newTimeLine;
	}

	/** Check if date is Sunday */
	private static boolean chedkDateIsSunday(LocalDateTime date) {
		return date.getDayOfWeek() == DayOfWeek.SUNDAY;
	}

	/** Check if date is Saturday */
	private static boolean chedkDateIsSaturday(LocalDateTime date) {
		return date.getDayOfWeek() == DayOfWeek.SATURDAY;
	}

	/**
	 * Add working days (Mon–Fri) to a base datetime. Skips Saturday & Sunday.
	 */
	public static LocalDateTime addWorkDays(LocalDateTime baseDateTime, int days, LocalTime workStart) {
		LocalDate resultDate = baseDateTime.toLocalDate();

		// Adjust if starting on weekend
		if (resultDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
			resultDate = days < 0 ? resultDate.minusDays(1) : resultDate.plusDays(2);
		} else if (resultDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
			resultDate = days < 0 ? resultDate.minusDays(2) : resultDate.plusDays(1);
		}

		// If days == 0, return adjusted result at workStart time
		if (days == 0) {
			return LocalDateTime.of(resultDate, workStart);
		}

		int absDays = Math.abs(days);
		int direction = days > 0 ? 1 : -1;

		for (int i = 0; i < absDays; i++) {
			resultDate = resultDate.plusDays(direction);
			// Skip weekends
			while (resultDate.getDayOfWeek() == DayOfWeek.SATURDAY || resultDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
				resultDate = resultDate.plusDays(direction);
			}
		}

		return LocalDateTime.of(resultDate, workStart);
	}

	/**
	 * Adjusts SLA considering working hours (9:30 AM – 6:30 PM), skipping Sundays.
	 */
	public LocalDateTime workingHoursTat(LocalDateTime startTime, int tatHours) {
		LocalTime WORK_START = LocalTime.of(9, 30);
		LocalTime WORK_END = LocalTime.of(18, 30);

		LocalDateTime expectedTime;

		// Case 1: Before working hours → start from today's 9:30 AM
		if (startTime.toLocalTime().isBefore(WORK_START)) {
			startTime = LocalDateTime.of(startTime.toLocalDate(), WORK_START);
		}

		// Case 2: After working hours → start from next day's 9:30 AM
		if (startTime.toLocalTime().isAfter(WORK_END)) {
			LocalDate nextDay = startTime.plusDays(1).toLocalDate();
			startTime = LocalDateTime.of(nextDay, WORK_START);
		}

		// Add TAT hours
		expectedTime = startTime.plusHours(tatHours);

		// Case 3: If expected time crosses end of day → move overflow to next day
		if (expectedTime.toLocalTime().isAfter(WORK_END)) {
			long extraMinutes = Duration.between(WORK_END, expectedTime.toLocalTime()).toMinutes();
			LocalDate nextDay = expectedTime.toLocalDate().plusDays(1);
			expectedTime = LocalDateTime.of(nextDay, WORK_START).plusMinutes(extraMinutes);
		}

		// Case 4: If result falls on Sunday → shift to Monday 9:30 AM
		if (expectedTime.getDayOfWeek() == DayOfWeek.SUNDAY) {
			expectedTime = LocalDateTime.of(expectedTime.toLocalDate().plusDays(1), WORK_START);
		}

		return expectedTime;
	}

	@Override
	public Map<String, Object> changeRequestTicket(CrTicketDTO dto, MultiValueMap<String, Object> headers)
			throws BussinessException, TechnicalException, ContractException {

		return null;
	}

}
