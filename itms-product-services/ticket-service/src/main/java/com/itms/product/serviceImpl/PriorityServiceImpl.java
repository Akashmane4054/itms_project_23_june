package com.itms.product.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itms.product.domain.PriorityCode;
import com.itms.product.repository.PriorityCodeRepository;

@Service
public class PriorityServiceImpl {

	@Autowired
	private PriorityCodeRepository priorityCodeRepository;

	public int getPriorityCode(String priorityLevel) {
		return priorityCodeRepository.findByPriorityLevel(priorityLevel).map(PriorityCode::getPriorityCode).orElse(0); // return
																														// 0
																														// if
																														// not
																														// found
	}
}