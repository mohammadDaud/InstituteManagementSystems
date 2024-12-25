package com.instituteManagementSystem.app.exceptions;

import java.io.IOException;
import java.util.Objects;

import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.instituteManagementSystem.app.entities.HttpResponse;

@RestControllerAdvice
public class ExceptionHandling implements ErrorController
{
	private final Logger logger=LoggerFactory.getLogger(getClass());
	private static final String ACCOUNT_LOCKED="Your account has been locked.Please contact administration";
	private static final String METHOD_IS_NOT_ALLOWED="this request method is not allowed on this endpoint.Please send a'%s' request";
	private static final String INTERNAL_SERVER_ERROR_MSG="An error occerred while processing the request";
	private static final String INCORRECT_CREDENTIALS="Username/Password incorrect.Please try agin";
	private static final String ACCOUNT_DISABLED="Your account has been disabled.If this is an error,Please contact administration";
	private static final String ERROR_PROCESSING_FILE="Error occerred while processing file";
	private static final String NOT_ENOUGH_PERMISSION="you do not have enough permission";
	private static final String ERROR_PATH="/error";
	
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<HttpResponse> accountDisabledException()
	{
		return createHttpResponse(HttpStatus.BAD_REQUEST, ACCOUNT_DISABLED);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<HttpResponse> badCredentialsException()
	{
		return createHttpResponse(HttpStatus.BAD_REQUEST, INCORRECT_CREDENTIALS);
	}
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<HttpResponse> accessDeniedException()
	{
		return createHttpResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION);
	}
	
	@ExceptionHandler(TokenExpiredException.class)
	public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException expired)
	{
		return createHttpResponse(HttpStatus.UNAUTHORIZED, expired.getMessage().toUpperCase());
	}

	@ExceptionHandler(LockedException.class)
	public ResponseEntity<HttpResponse> accountLockedException()
	{
		return createHttpResponse(HttpStatus.UNAUTHORIZED, ACCOUNT_LOCKED);
	}
	@ExceptionHandler(EmailExistException.class)
	public ResponseEntity<HttpResponse> emailExistException(EmailExistException email)
	{
		return createHttpResponse(HttpStatus.BAD_REQUEST, email.getMessage().toUpperCase());
	}
	@ExceptionHandler(UsernameExistException.class)
	public ResponseEntity<HttpResponse> UsernameExistException(UsernameExistException username)
	{
		return createHttpResponse(HttpStatus.BAD_REQUEST, username.getMessage().toUpperCase());
	}
	@ExceptionHandler(EmailNotFoundException.class)
	public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception)
	{
		return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage().toUpperCase());
	}
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException exception)
	{
		logger.error(exception.getMessage());
		return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage().toUpperCase());
	}
	@ExceptionHandler(NotAnImageFileException.class)
	public ResponseEntity<HttpResponse> notAnImageFileException(NotAnImageFileException exception)
	{
		logger.error(exception.getMessage());
		return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage().toUpperCase());
	}
	
	/* ====White able error Handling======(1)====
	 * @ExceptionHandler(NoHandlerFoundException.class) public
	 * ResponseEntity<HttpResponse>
	 * methodNotSupportedException(NoHandlerFoundException e) { return
	 * createHttpResponse(HttpStatus.BAD_REQUEST, "This page was not found"); }
	 */
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception)
	{
		HttpMethod supportedmethod=Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
		return createHttpResponse(HttpStatus.METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedmethod));
	}
	@ExceptionHandler(Exception.class)
	public ResponseEntity<HttpResponse> iternalServerErrorException(Exception exception)
	{
		logger.error(exception.getMessage());
		return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR,INTERNAL_SERVER_ERROR_MSG);
	}
	@ExceptionHandler(NoResultException.class)
	public ResponseEntity<HttpResponse> notFoundException(NoResultException exception)
	{
		logger.error(exception.getMessage());
		return createHttpResponse(HttpStatus.NOT_FOUND,exception.getMessage().toUpperCase());
	}
	@ExceptionHandler(IOException.class)
	public ResponseEntity<HttpResponse> iOException(IOException exception)
	{
		logger.error(exception.getMessage());
		return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR,ERROR_PROCESSING_FILE);
	}
	
	
	private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus,String message)
	{
		return new ResponseEntity<>(new HttpResponse(httpStatus.value(), 
										    		 httpStatus, 
													 httpStatus.getReasonPhrase().toUpperCase(),
													 message.toUpperCase()
													 ),
						    		 httpStatus);
	}
/*======white able error handling second way====(2)=====*/
	@RequestMapping(ERROR_PATH)
	public ResponseEntity<HttpResponse> notFound404()
	{
		return createHttpResponse(HttpStatus.NOT_FOUND,"There is no mapping for this URL");
	}
	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}
}
