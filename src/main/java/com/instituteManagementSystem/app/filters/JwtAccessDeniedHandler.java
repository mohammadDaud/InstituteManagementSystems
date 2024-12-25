package com.instituteManagementSystem.app.filters;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.instituteManagementSystem.app.constent.SecurityConstents;
import com.instituteManagementSystem.app.entities.HttpResponse;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException exception) throws IOException, ServletException 
	{
		HttpResponse  httpResponse=new HttpResponse(HttpStatus.UNAUTHORIZED.value(), 
				HttpStatus.UNAUTHORIZED, 
				HttpStatus.UNAUTHORIZED.getReasonPhrase(), 
				SecurityConstents.ACCESS_DENIED_MESSAGE);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		OutputStream outputStream=response.getOutputStream();
		ObjectMapper mapper=new ObjectMapper();
		mapper.writeValue(outputStream, httpResponse);
		outputStream.flush();
	}

}
