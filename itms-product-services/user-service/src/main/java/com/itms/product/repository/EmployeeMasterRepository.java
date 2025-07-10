package com.itms.product.repository;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.EmployeeMaster;

import jakarta.transaction.Transactional;

@Repository
public interface EmployeeMasterRepository extends JpaRepository<EmployeeMaster, String> {

	public EmployeeMaster findByEmpId(String empId);

	public EmployeeMaster findByEmpName(String empName);

	@Query("SELECT COUNT(e) FROM employee_master e WHERE e.empId = :empId")
	long countByEmpId(@Param("empId") String empId);

	public EmployeeMaster findByPredecessor(String empId);

	@Query(value = "SELECT MAXLOGINCOUNT FROM employee_master WHERE EMP_ID = :empId", nativeQuery = true)
	Integer findMaxLoginCountByEmpId(@Param("empId") String empId);

	@Query(value = "SELECT LASTFAILEDLOGINTIME FROM employee_master WHERE EMP_ID = :empId", nativeQuery = true)
	Timestamp getLastFailedLoginTime(@Param("empId") String empId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE employee_master SET LOGINCOUNT = 0, LASTFAILEDLOGINTIME = NULL WHERE EMP_ID = :empId", nativeQuery = true)
	int resetLoginCount(@Param("empId") String empId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE employee_master SET MAXLOGINCOUNT = :count, LASTFAILEDLOGINTIME = :lastFailedTime WHERE EMP_ID = :empId", nativeQuery = true)
	int updateLoginCountAndFailedTime(@Param("count") int count, @Param("lastFailedTime") Timestamp lastFailedTime,
			@Param("empId") String empId);
	
	
	 @Query(value = "SELECT COUNT(*) FROM EMPLOYEE_MASTER WHERE EMP_ID = :empId AND PASSWORD = :password", nativeQuery = true)
	    int countValidUser(@Param("empId") String empId, @Param("password") String password);


}
