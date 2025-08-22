package com.itms.product.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.itms.product.domain.EmployeeMaster;
import com.itms.product.repository.EmployeeMasterRepository;

@Service
public class EmployeeDetailsService implements UserDetailsService {

	private final EmployeeMasterRepository employeeRepo;

	@Autowired
	public EmployeeDetailsService(EmployeeMasterRepository employeeRepo) {
		this.employeeRepo = employeeRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String empId) throws UsernameNotFoundException {
		EmployeeMaster employee = employeeRepo.findByEmpId(empId);

		if (employee == null) {
			throw new UsernameNotFoundException("Employee not found with empId: " + empId);
		}

		return User.builder().username(employee.getEmpId()).password(employee.getPassword()) // must already be encoded
//	              .authorities(Collections.singletonList(() -> employee.getRole())) // enable if you have role field
				.build();
	}
}
