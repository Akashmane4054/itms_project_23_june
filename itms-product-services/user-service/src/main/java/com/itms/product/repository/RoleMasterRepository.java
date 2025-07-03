package com.itms.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.RoleMaster;

@Repository
public interface RoleMasterRepository extends JpaRepository<RoleMaster, Integer> {

	@Query("SELECT r.roleTag FROM RoleMaster r WHERE r.roleId = :roleId")
	String findRoleTagByRoleId(@Param("roleId") Integer roleId);

}
