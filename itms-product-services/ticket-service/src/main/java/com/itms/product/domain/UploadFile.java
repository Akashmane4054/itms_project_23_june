package com.itms.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "UPLOADFILE")
@Data
public class UploadFile {

	@Id
	@Column(name = "ATTACHMENT_ID", nullable = false)
	private Long attachmentId;

	@Column(name = "FILENAME", length = 250)
	private String fileName;

	@Column(name = "FILEEXTENSION", length = 200)
	private String fileExtension;

	@Lob
	@Column(name = "FILEUP")
	private byte[] fileUp;

	@Column(name = "IR_NUMBER", length = 20)
	private String irNumber;
}