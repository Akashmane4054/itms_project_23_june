package com.itms.product.domain;

import jakarta.persistence.Column;
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
@Table(name = "state_master")
public class StateMaster {

	@Id
	@Column(name = "STATE_CODE")
	private Long stateCode;

	@Column(name = "STATE_NAME")
	private String stateName;

	@Column(name = "STATE_SHORT_NAME")
	private String stateShortName;

	@Column(name = "STATE_FLAG")
	private String stateFlag;

}