package com.itms.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.RegisterMaster;

import jakarta.transaction.Transactional;

@Repository
public interface RegisterMasterRepository extends JpaRepository<RegisterMaster, String> {

}