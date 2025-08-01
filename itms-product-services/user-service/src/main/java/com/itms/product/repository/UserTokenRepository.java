package com.itms.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.UserToken;

import jakarta.transaction.Transactional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, String> {

	List<UserToken> findTokensByEmpId(String empId);

	@Query("SELECT u FROM UserToken u WHERE u.empId = :empId AND u.loggedIn = true AND u.expiration > CURRENT_TIMESTAMP")
	List<UserToken> findByEmpIdAndLoggedInTrue(@Param("empId") String empId);

	@Modifying
	@Transactional
	@Query("DELETE FROM UserToken u WHERE u.token = :token")
	void deleteByToken(@Param("token") String token);

	@Modifying
	@Transactional
	@Query("UPDATE UserToken u SET u.loggedIn = :status WHERE u.empId = :empId")
	void updateLoggedInStatus(@Param("empId") String empId, @Param("status") Boolean status);
}