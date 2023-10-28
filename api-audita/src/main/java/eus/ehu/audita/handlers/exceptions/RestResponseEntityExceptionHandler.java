package eus.ehu.audita.handlers.exceptions;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import eus.ehu.audita.exceptions.MissingRequestParameterException;
import eus.ehu.audita.exceptions.NoValidSortByFieldException;
import eus.ehu.audita.utils.Constantes;


@ControllerAdvice
public class RestResponseEntityExceptionHandler {

	private static final Logger logger = LogManager.getLogger(RestResponseEntityExceptionHandler.class);
	
	@Autowired
	MessageSource mensajes;
	
	@ExceptionHandler(value = {
			EmptyResultDataAccessException.class,
			NoHandlerFoundException.class,})
    public ResponseEntity<APIError> handleNoHandlerFoundException(Exception ex, HttpServletRequest request) {
		String message = mensajes.getMessage("error.404", null, LocaleContextHolder.getLocale());
		return handle(message, ex, request, HttpStatus.NOT_FOUND);
    }
	
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<APIError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
		
		String message = ex.getFieldErrors()
				.stream()
				.map(e -> mensajes.getMessage(e.getDefaultMessage(), null, LocaleContextHolder.getLocale()))
				.reduce(mensajes.getMessage("errors.found", null, LocaleContextHolder.getLocale()), String::concat);
		return handle(message, ex, request, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = NoValidSortByFieldException.class)
	public ResponseEntity<APIError> handleNoValidSortByFieldException(NoValidSortByFieldException ex, HttpServletRequest request) {
		String message = mensajes.getMessage("error.parametroOrdenacionNovalido", new String[] {ex.getMessage()}, LocaleContextHolder.getLocale());
		return handle(message, ex, request, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = MissingRequestParameterException.class)
	public ResponseEntity<APIError> handleMissingRequestParameterException(MissingRequestParameterException ex, HttpServletRequest request) {
		String message = mensajes.getMessage(ex.getMessage(), null, LocaleContextHolder.getLocale());
		return handle(message, ex, request, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = {
			MethodArgumentTypeMismatchException.class,
			HttpMessageNotReadableException.class,
			BindException.class,
			IllegalArgumentException.class,
			ConstraintViolationException.class,
			HttpMediaTypeNotAcceptableException.class})
	public ResponseEntity<APIError> handleBadRequests(Exception ex, HttpServletRequest request) {
		String message = mensajes.getMessage("error.400", null, LocaleContextHolder.getLocale());
		return handle(message, ex, request, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<APIError> handleException(Exception ex, HttpServletRequest request) {
		logger.error(ex.getMessage(), ex);
		String message = mensajes.getMessage("error.500", null, LocaleContextHolder.getLocale());
		return handle(message, ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	private ResponseEntity<APIError> handle(String message, Exception ex, HttpServletRequest request, HttpStatus httpStatus) {
		logger.error(ex.getMessage());
		
		APIError apierror = createApiError(message, request, httpStatus);
		
		HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.CONTENT_TYPE, Constantes.RESPONSE_HEADER_CONTENT_TYPE_KO);
		
		return new ResponseEntity<>(apierror, headers, httpStatus);
	}
	
	private APIError createApiError(String message, HttpServletRequest request, HttpStatus status) {
		return new APIError(
				status.value(),
				status.name(), 
				message,
				request.getRequestURI());
	}
	

}