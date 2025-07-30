package com.itms.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.CallHistoryMaster;

@Repository
public interface CallHistoryRepository extends JpaRepository<CallHistoryMaster, Long> {

}
