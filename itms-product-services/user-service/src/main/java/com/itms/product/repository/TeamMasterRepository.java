package com.itms.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.TeamMaster;

import jakarta.transaction.Transactional;

@Repository
public interface TeamMasterRepository extends JpaRepository<TeamMaster, Long> {

	Optional<TeamMaster> findByModuleName(String moduleName);

	@Modifying
	@Transactional
	@Query("UPDATE TeamMaster t SET t.empId = :empId, t.owner = :owner WHERE t.moduleCode = :moduleCode")
	int updateTeamOwner(@Param("empId") String empId, @Param("owner") String owner,
			@Param("moduleCode") Long moduleCode);

}