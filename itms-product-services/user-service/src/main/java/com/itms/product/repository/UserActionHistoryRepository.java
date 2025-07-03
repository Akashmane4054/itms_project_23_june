package com.itms.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.UserActionHistory;

@Repository
public interface UserActionHistoryRepository extends JpaRepository<UserActionHistory, String> {


}
