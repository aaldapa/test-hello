package eus.ehu.audita.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Audit {

	private String requestId;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime requestDate;
	
	private String requestHeaderAcceptLanguage;
	
	private String requestMethod;
	
	private String requestPath;
	
	private String requestBody;
	
	private Integer responseHttpCode;
	
	private String responseHeaderContentType;
	
	private String responseBody;

}