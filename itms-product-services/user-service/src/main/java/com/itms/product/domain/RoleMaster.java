package com.itms.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ROLE_MASTER")
@Data
@NoArgsConstructor
public class RoleMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ROLE_ID")
	private Integer roleId;

	@Column(name = "ROLE_NAME", length = 200)
	private String roleName;

	@Column(name = "ROLE_TAG", length = 20)
	private String roleTag;
}