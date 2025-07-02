package com.itms.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.TeamMaster;

@Repository
public interface TeamMasterRepository extends JpaRepository<TeamMaster, Long> {

	Optional<TeamMaster> findByModuleName(String moduleName);

}