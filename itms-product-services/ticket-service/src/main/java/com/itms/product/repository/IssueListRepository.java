package com.itms.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itms.product.domain.IssueList;

@Repository
public interface IssueListRepository extends JpaRepository<IssueList, String> {

	Optional<IssueList> findByTeamNameAndIssue(String teamName, String issue);
}
