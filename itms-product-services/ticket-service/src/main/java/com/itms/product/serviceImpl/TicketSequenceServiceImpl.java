package com.itms.product.serviceImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketSequenceServiceImpl {

	private final JdbcTemplate jdbcTemplate;

	public String generateTicketNumber(String bankGroup, String bankType) {
		String ticketSeq = bankGroup + bankType;

		String query = switch (ticketSeq) {
		case "RRT" -> "SELECT nxt_RRT_issue_seq FROM dual";
		case "RRS" -> "SELECT nxt_RRS_issue_seq FROM dual";
		case "RRC" -> "SELECT nxt_RRC_issue_seq FROM dual";
		case "RRI" -> "SELECT nxt_RRI_issue_seq FROM dual";
		case "UCT" -> "SELECT nxt_UCT_issue_seq FROM dual";
		case "UCS" -> "SELECT nxt_UCS_issue_seq FROM dual";
		case "UCC" -> "SELECT nxt_UCC_issue_seq FROM dual";
		case "UCI" -> "SELECT nxt_UCI_issue_seq FROM dual";
		case "CBT" -> "SELECT nxt_CBT_issue_seq FROM dual";
		case "CBS" -> "SELECT nxt_CBS_issue_seq FROM dual";
		case "CBC" -> "SELECT nxt_CBC_issue_seq FROM dual";
		case "CBI" -> "SELECT nxt_CBI_issue_seq FROM dual";
		case "NBT" -> "SELECT nxt_NBT_issue_seq FROM dual";
		case "NBS" -> "SELECT nxt_NBS_issue_seq FROM dual";
		case "NBC" -> "SELECT nxt_NBC_issue_seq FROM dual";
		case "NBI" -> "SELECT nxt_NBI_issue_seq FROM dual";
		case "CAT" -> "SELECT nxt_CAT_issue_seq FROM dual";
		case "CAS" -> "SELECT nxt_CAS_issue_seq FROM dual";
		case "CAC" -> "SELECT nxt_CAC_issue_seq FROM dual";
		case "CAI" -> "SELECT nxt_CAI_issue_seq FROM dual";
		case "INT" -> "SELECT nxt_INT_issue_seq FROM dual";
		case "INS" -> "SELECT nxt_INS_issue_seq FROM dual";
		case "INC" -> "SELECT nxt_INC_issue_seq FROM dual";
		case "INI" -> "SELECT nxt_INI_issue_seq FROM dual";
		case "TNT" -> "SELECT nxt_TNT_issue_seq FROM dual";
		case "TNS" -> "SELECT nxt_TNS_issue_seq FROM dual";
		case "TNC" -> "SELECT nxt_TNC_issue_seq FROM dual";
		case "TNI" -> "SELECT nxt_TNI_issue_seq FROM dual";
		case "NCT" -> "SELECT nxt_NCT_issue_seq FROM dual";
		case "NCS" -> "SELECT nxt_NCS_issue_seq FROM dual";
		case "NCC" -> "SELECT nxt_NCC_issue_seq FROM dual";
		case "NCI" -> "SELECT nxt_NCI_issue_seq FROM dual";
		case "WBT" -> "SELECT nxt_WBT_issue_seq FROM dual";
		case "WBS" -> "SELECT nxt_WBS_issue_seq FROM dual";
		case "WBC" -> "SELECT nxt_WBC_issue_seq FROM dual";
		case "WBI" -> "SELECT nxt_WBI_issue_seq FROM dual";
		default -> throw new IllegalArgumentException("Invalid ticket sequence: " + ticketSeq);
		};

		return jdbcTemplate.queryForObject(query, String.class);
	}
}
