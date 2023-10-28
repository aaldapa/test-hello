package eus.ehu.audita.handlers.exceptions;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class APIError{
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime timestamp;
	private int status;
	private String error;
	private String message;
	private String path;
	 
	public APIError(int status, String error, String message, String path) {
		super();
		this.status = status;
		this.error = error;
		this.message = message;
		this.path = path;
		timestamp = LocalDateTime.now();	
	}
	
}
