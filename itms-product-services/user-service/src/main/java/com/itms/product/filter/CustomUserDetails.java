package com.itms.product.filter;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomUserDetails extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String source;

	public CustomUserDetails(String empId, String password, Collection<? extends GrantedAuthority> authorities,
			String source) {
		super(empId, password != null ? password : "", authorities);
		this.source = source;
	}

	public String getSource() {
		return source;
	}

}
