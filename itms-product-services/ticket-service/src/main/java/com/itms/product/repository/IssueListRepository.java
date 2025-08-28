package com.itms.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.IssueList;

@Repository
public interface IssueListRepository extends JpaRepository<IssueList, Integer> {

	Optional<IssueList> findByTeamNameAndIssue(String teamName, String issue);

	@Query("SELECT i.slaLevel FROM IssueList i WHERE i.teamName = :team AND i.issue = :issue")
	String findSlaByTeamAndIssue(@Param("team") String team, @Param("issue") String issue);

	@Query("SELECT i.flag24hours FROM IssueList i WHERE i.teamName = :teamName AND i.issue = :issue")
	String findFlag24hours(@Param("teamName") String teamName, @Param("issue") String issue);

	@Query("SELECT i.slaTime FROM IssueList i WHERE i.teamName = :teamName AND i.issue = :issue")
	String findSlaTime(@Param("teamName") String teamName, @Param("issue") String issue);

	@Query("SELECT i.slaTime FROM IssueList i WHERE i.teamName = :teamName AND i.issue = :issue")
	String findSlaTimeByTeamNameAndIssue(String teamName, String issue);

}
