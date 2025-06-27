package com.itms.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.EmployeeMaster;

@Repository
public interface EmployeeMasterRepository extends JpaRepository<EmployeeMaster, Long> {

	public EmployeeMaster findByEmpId(String empId);

	public EmployeeMaster findByEmpName(String empName);

}
