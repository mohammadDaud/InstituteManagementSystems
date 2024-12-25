package com.instituteManagementSystem.app.entities;

import java.util.Date;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;


//{ JSON RETURN LIKE THIS
//code:200,
//httpStatus:"OK",
//reason:"OK",
//message:"Your request was successful"
//}


public class HttpResponse 
{
	@JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="dd-mm-yyyy hh:mm:ss",timezone = "India/Mumbai" )
	private int httpStatusCode;
	private HttpStatus httpStatus;
	private String reason;
	private String message;
	private Date timeStamp;

	public HttpResponse() {}
	public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) 
	{
		this.timeStamp=new Date();
		this.httpStatusCode = httpStatusCode;//200,201,400,500
		this.httpStatus = httpStatus;
		this.reason = reason;
		this.message = message;
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}
	public void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}






}
