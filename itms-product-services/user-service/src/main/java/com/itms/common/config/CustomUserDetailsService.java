package com.itms.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.itms.common.domain.EmployeeMaster;
import com.itms.common.repository.EmployeeMasterRepository;

public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private EmployeeMasterRepository employeeMasterRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		EmployeeMaster employee = employeeMasterRepository.findByEmpName(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

		return User.builder().username(employee.getEmail()).password(employee.getPassword()) // must be encrypted
																								// (BCrypt) in DB
				.build();
	}

}
