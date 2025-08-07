package com.itms.product.serviceImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Comparator;
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
import com.itms.product.dto.TicketRequestDTO;
import com.itms.product.repository.BankDetailsRepository;
import com.itms.product.repository.CallHistoryRepository;
import com.itms.product.repository.CallMasterRepository;
import com.itms.product.repository.IssueListRepository;
import com.itms.product.repository.PriorityCodeRepository;
import com.itms.product.repository.SlaMasterRepository;
import com.itms.product.repository.TicketRuleRepository;
import com.itms.product.repository.UploadFileRepository;
import com.itms.product.service.ticketService;

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

	@Override
	public Map<String, Object> issueTicket(TicketRequestDTO dto, MultiValueMap<String, String> headers)
			throws BussinessException, TechnicalException, ContractException {

		log.info("Ticket Creation: START");
		Map<String, Object> responseMap = new HashMap<>();

		Map<String, Object> userResponse = verifyToken(headers);

		Long empId = Long.parseLong(String.valueOf(userResponse.get("empId")));

		try {
			// Step 1: Get bank details
			BankDetails bankDetails = bankDetailsRepository.findByBankName(dto.getBankname());

			// Step 2: Generate Unique Ticket ID
			String ticketId = ticketSequenceService.generateTicketNumber(bankDetails.getBankGroup(), "T");

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

	private void handleFileUploads(TicketRequestDTO dto, String ticketId) throws IOException, SQLException {
		MultipartFile brdFile = dto.getBrdBusinessFile();
		MultipartFile circularFile = dto.getCircularUploadFile();

		if (brdFile != null && !brdFile.isEmpty()) {
			uploadBrd(brdFile, ticketId);
		}

		if (circularFile != null && !circularFile.isEmpty()) {
			uploadCircular(circularFile, ticketId);
		}
	}

	private CallMaster buildCallMasterEntity(TicketRequestDTO dto, int bankCode, String ticketId) throws SQLException {
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

	private CallHistoryMaster buildCallHistoryEntity(TicketRequestDTO dto, String ticketId, int moduleCode,
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

	private Map<String, Object> verifyToken(MultiValueMap<String, String> headers)
			throws BussinessException, ContractException, TechnicalException {
		return ExceptionUtil.throwExceptionsIfPresent(userServiceUtil.tokenVerification(headers));
	}

}
