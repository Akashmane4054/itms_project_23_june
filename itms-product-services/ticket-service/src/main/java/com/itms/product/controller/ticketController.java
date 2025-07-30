package com.itms.product.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.core.util.EndPointReference;
import com.itms.core.util.LogUtil;
import com.itms.product.dto.TicketRequestDTO;
import com.itms.product.service.ticketService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ticketController {

	@Autowired
	private ticketService ticketService;

	@PostMapping(EndPointReference.ISSUE_TICKET)
	public Map<String, Object> issueTicket(@RequestBody TicketRequestDTO dto)
			throws BussinessException, TechnicalException, ContractException {
		log.info(LogUtil.presentationLogger(EndPointReference.ISSUE_TICKET));
		return ticketService.issueTicket(dto);
	}

}
