package eus.ehu.audita.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MissingRequestParameterException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public MissingRequestParameterException(String message) {
		super(message);
	}
	
	public MissingRequestParameterException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingRequestParameterException() {
		super("error.parametrosNecesarios");
	}
	 
 }
