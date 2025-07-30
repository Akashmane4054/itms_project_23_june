package com.itms.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.CallMaster;

@Repository
public interface CallMasterRepository extends JpaRepository<CallMaster, String> {

}
