package eus.ehu.audita.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class NoValidSortByFieldException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public NoValidSortByFieldException(String message) {
		super(message);
	}
	
	public NoValidSortByFieldException(String message, Throwable cause) {
		super(message, cause);
	}
	 
 }
