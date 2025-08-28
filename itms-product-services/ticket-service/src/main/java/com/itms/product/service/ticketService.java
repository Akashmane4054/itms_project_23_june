package com.itms.product.service;

import java.util.Map;

import org.springframework.util.MultiValueMap;

import com.itms.core.exception.BussinessException;
import com.itms.core.exception.ContractException;
import com.itms.core.exception.TechnicalException;
import com.itms.product.dto.CrTicketDTO;
import com.itms.product.dto.IssueTicketRequestDTO;
import com.itms.product.dto.ServiceTicketDTO;

public interface ticketService {

	Map<String, Object> issueTicket(IssueTicketRequestDTO dto, MultiValueMap<String, Object> headers)
			throws BussinessException, TechnicalException, ContractException;

	Map<String, Object> serviceTicket(ServiceTicketDTO dto, MultiValueMap<String, Object> headers)
			throws BussinessException, TechnicalException, ContractException;

	Map<String, Object> changeRequestTicket(CrTicketDTO dto, MultiValueMap<String, Object> headers)
			throws BussinessException, TechnicalException, ContractException;

}
