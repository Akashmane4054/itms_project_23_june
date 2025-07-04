package com.itms.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.EmployeeMaster;

@Repository
public interface EmployeeMasterRepository extends JpaRepository<EmployeeMaster, String> {

	public EmployeeMaster findByEmpId(String empId);

	public EmployeeMaster findByEmpName(String empName);

	@Query("SELECT COUNT(e) FROM EmployeeMaster e WHERE e.empId = :empId")
	long countByEmpId(@Param("empId") String empId);
	
	public EmployeeMaster findByPredecessor(String empId);


}
