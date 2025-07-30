package com.itms.product.serviceImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.product.domain.BankDetails;
import com.itms.product.domain.CallHistoryMaster;
import com.itms.product.domain.CallMaster;
import com.itms.product.domain.IssueList;
import com.itms.product.dto.TicketRequestDTO;
import com.itms.product.repository.BankDetailsRepository;
import com.itms.product.repository.CallHistoryRepository;
import com.itms.product.repository.CallMasterRepository;
import com.itms.product.repository.IssueListRepository;
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
	private PriorityServiceImpl priorityService;

	@Autowired
	private IssueListRepository issueListRepository;

	@Autowired
	private BankDetailsRepository bankDetailsRepository;

	@Override
	public Map<String, Object> issueTicket(TicketRequestDTO dto)
			throws BussinessException, TechnicalException, ContractException {

		log.info("Ticket Creation: START");
		Map<String, Object> responseMap = new HashMap<>();

		try {

			// 1. Generate Unique Ticket ID
			String bankGroup = getBankGroup(dto.getBankname());
			String ticketId = ticketSequenceService.generateTicketNumber(bankGroup, "T");

			// 2. Upload Files (if any)
			handleFileUploads(dto, ticketId);

			// 3. Prepare CallMaster Entity
			CallMaster callMaster = buildCallMasterEntity(dto, ticketId);

			// Save CallMaster Entry
			callMasterRepository.save(callMaster);

			// 4. Create CallHistory Entry
			CallHistoryMaster callHistory = buildCallHistoryEntity(dto, ticketId, callMaster.getCallPriorityCode());
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

	private void handleFileUploads(TicketRequestDTO dto, String ticketId) throws IOException, SQLException {
		MultipartFile brdFile = dto.getBrdBusinessFile();
		MultipartFile circularFile = dto.getCircularUploadFile();

		if (brdFile != null && !brdFile.isEmpty()) {
//			IRManager.uploadBrd(brdFile, ticketId);
		}

		if (circularFile != null && !circularFile.isEmpty()) {
//			IRManager.uploadCircular(circularFile, ticketId);
		}
	}

	private CallMaster buildCallMasterEntity(TicketRequestDTO dto, String ticketId) throws SQLException {
		CallMaster callMaster = new CallMaster();

		callMaster.setCallNo(ticketId);
		callMaster.setCallDesc(dto.getIssueDescription());
		callMaster.setCallIssueRemark(dto.getIssueRemark());
		callMaster.setCallBankBranch(dto.getBankRegion());
		callMaster.setCallBankName(Integer.parseInt(dto.getBankname()));
		callMaster.setCallAssignedModuleCode(0); // To be updated based on rules
		callMaster.setCallAssignTo(dto.getAssigntoCRE());
		callMaster.setCallPriority(dto.getPriority());
		callMaster.setCallSeverityLevel(dto.getSeverityLevel());
		callMaster.setCallStatusCode(0); // Default or workflow status
		callMaster.setCallInitiator(dto.getUserId());

		LocalDateTime now = LocalDateTime.now();
		callMaster.setCallDate(now);
		callMaster.setExpectedTime(now);

		int priorityCode = priorityService.getPriorityCode(dto.getPriority());
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

	private CallHistoryMaster buildCallHistoryEntity(TicketRequestDTO dto, String ticketId, int priorityCode) {
		CallHistoryMaster history = new CallHistoryMaster();

		// Extract SLA and handle fallback
		String sla = dto.getSla();
		String parsedSla = (sla != null && sla.length() >= 2) ? sla.substring(0, 2) : "";
		String slaFallbackCheck = "( ";

		String issueDescription = dto.getIssueDescription();
		String teamName = dto.getAssigntoCRE(); // Maps to TEAM_NAME column in DB

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
		history.setFromModule(0); // To be updated via workflow
		history.setToModule(0); // To be updated via workflow
		history.setToMember(dto.getAssigntoCRE());
		history.setPriorityCode(priorityCode);
		history.setStatusCode(0); // To be updated via workflow
		history.setSlaId(dto.getSla());

		return history;
	}

	public String resolveSlaLevel(String teamName, String issueDescription) {
		return issueListRepository.findByTeamNameAndIssue(teamName, issueDescription).map(IssueList::getSlaLevel)
				.orElse(null);
	}

	public String getBankGroup(String bankName) {
		return bankDetailsRepository.findByBankName(bankName).map(BankDetails::getBankGroup).orElse("IN"); // Default
																											// fallback
																											// if not
																											// found
	}

}
