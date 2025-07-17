package com.itms.core.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BussinessException.class)
	public ResponseEntity<Object> handleBussinessException(BussinessException ex) {
		return buildErrorResponse(ex.getErrorcode(), "Business Error", ex.getDescription(), ex.getParameter());
	}

	@ExceptionHandler(TechnicalException.class)
	public ResponseEntity<Object> handleTechnicalException(TechnicalException ex) {
		return buildErrorResponse(ex.getErrorcode(), "Technical Error", ex.getMessage(), null);
	}

	@ExceptionHandler(ContractException.class)
	public ResponseEntity<Object> handleContractException(ContractException ex) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST, "Contract Error", ex.getMessage(), null);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGenericException(Exception ex) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected Error", ex.getMessage(), null);
	}

	private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String errorType, String message,
			String parameter) {
		Map<String, Object> errorBody = new HashMap<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

		errorBody.put("timestamp", LocalDateTime.now().format(formatter));
		errorBody.put("status", status.value());
		errorBody.put("error", errorType);
		errorBody.put("message", message);
		errorBody.put("parameter", parameter);

		return new ResponseEntity<>(errorBody, status);
	}

}