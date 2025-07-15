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

	@Query("SELECT COUNT(e) FROM EmployeeMaster e WHERE e.empId = :empId")
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

	@Query(value = "SELECT COUNT(*) FROM employee_master WHERE EMP_ID = :empId AND PASSWORD = :password", nativeQuery = true)
	int countValidUser(@Param("empId") String empId, @Param("password") String password);

//	@Modifying
//	@Transactional
//	@Query("UPDATE EmployeeMaster e SET e.loggedIn = :status WHERE e.empId = :empId")
//	void updateLoggedInStatus(@Param("empId") String empId, @Param("status") String status);

//	@Modifying
//	@Transactional
//	@Query("UPDATE EmployeeMaster e SET e.lastLogin = CURRENT_TIMESTAMP, e.loggedIn = 'true' WHERE e.empId = :empId")
//	void updateLastLogin(@Param("empId") String empId);
//	
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE employee_master SET LAST_LOGIN = NOW(), LOGGED_IN = 'true' WHERE EMP_ID = :empId", nativeQuery = true)
	int updateLastLogin(@Param("empId") String empId);



}
