package com.itms.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itms.common.domain.EmployeeMaster;

@Repository
public interface EmployeeMasterRepository extends JpaRepository<EmployeeMaster, Long> {

	public EmployeeMaster findByEmpId(Long empId);

	public EmployeeMaster findByEmpName(String empName);

}
