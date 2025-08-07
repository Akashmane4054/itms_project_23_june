package com.itms.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.itms.product.domain.TicketRule;

import feign.Param;

public interface TicketRuleRepository extends JpaRepository<TicketRule, Long> {

	 @Query("SELECT t.empId FROM TicketRule t WHERE t.teamCode = :moduleCode AND t.bankType = :bankGroup AND t.issue = :issueDescription")
	    Optional<String> findEmpIdByRule(@Param("moduleCode") int moduleCode, 
	                                     @Param("bankGroup") String bankGroup, 
	                                     @Param("issueDescription") String issueDescription);

	    @Query("SELECT t.empId FROM TicketRule t WHERE t.teamCode = :moduleCode AND t.bankType = :bankGroup AND t.issue = " +
	           "(SELECT i.issue FROM IssueList i WHERE i.oldIssue = :oldIssue AND i.teamCode = :moduleCode)")
	    Optional<String> findEmpIdByOldIssueMapping(@Param("moduleCode") int moduleCode, 
	                                                @Param("bankGroup") String bankGroup, 
	                                                @Param("oldIssue") String oldIssue);
}
