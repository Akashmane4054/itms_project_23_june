package com.itms.product.domain;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TOKEN_LOG")
public class UserToken {

	@Id
	private String empId;

	private String token;

	private Date expiration;

	private Boolean loggedIn;

}
