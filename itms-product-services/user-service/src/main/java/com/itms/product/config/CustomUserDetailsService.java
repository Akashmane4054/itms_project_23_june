package com.itms.product.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.itms.product.domain.EmployeeMaster;
import com.itms.product.repository.EmployeeMasterRepository;

public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private EmployeeMasterRepository employeeMasterRepository;	
	
	 @Override
	  public UserDetails loadUserByUsername(String empId) throws UsernameNotFoundException {
	    User user = employeeMasterRepository.findByEmpId(empId)
	      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

	    return new org.springframework.security.core.userdetails.User(
	      user.getUsername(), user.getPassword(),
	      Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
	    );
	  }

}
