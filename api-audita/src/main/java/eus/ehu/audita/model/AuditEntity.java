package eus.ehu.audita.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="TEST_AUDIT")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditEntity {

	@Id
	@Column(name = "REQUEST_ID")
	private String requestId;
	
	@Column(name = "REQUEST_DATE")
	private LocalDateTime requestDate;
	
	@Column(name = "REQUEST_HEADER_ACCEPT_LANGUAGE")
	private String requestHeaderAcceptLanguage;
	
	@Column(name = "REQUEST_METHOD")
	private String requestMethod;
	
	@Column(name = "REQUEST_PATH")
	private String requestPath;
	
	@Column(name = "REQUEST_BODY", length = 1000)
	private String requestBody;
	
	@Column(name = "RESPONSE_HTTP_CODE")
	private Integer responseHttpCode;
	
	@Column(name = "RESPONSE_HEADER_CONTENT_TYPE")
	private String responseHeaderContentType;
	
	@Column(name = "RESPONSE_BODY", length = 20000)
	@Lob
	private String responseBody;

}