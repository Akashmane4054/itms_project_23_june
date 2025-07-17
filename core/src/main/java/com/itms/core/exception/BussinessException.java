package com.itms.core.exception;

import org.springframework.http.HttpStatus;

public class BussinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private HttpStatus errorcode;
	private String description;
	private String parameter;

	public BussinessException(HttpStatus errorcode, String description) {
		super(description);
		this.errorcode = errorcode;
		this.description = description;
	}

	public BussinessException(HttpStatus errorcode, String description, String parameter) {
		super(description);
		this.errorcode = errorcode;
		this.description = description;
		this.parameter = parameter;
	}

	public HttpStatus getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(HttpStatus errorcode) {
		this.errorcode = errorcode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
}
