package com.itms.product.service;

import java.util.Map;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.product.dto.TicketRequestDTO;

public interface ticketService {

	Map<String, Object> issueTicket(TicketRequestDTO dto)
			throws BussinessException, TechnicalException, ContractException;

}
