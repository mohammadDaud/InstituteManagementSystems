package com.instituteManagementSystem.app.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import com.instituteManagementSystem.app.service.LoginAttemptService;

@Component
public class AuthenticateFailureListener 
{
	private LoginAttemptService attemptService;

	@Autowired
	public AuthenticateFailureListener(LoginAttemptService attemptService) {

		this.attemptService = attemptService;
	}
	
	@EventListener
	public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event)
	{
		Object principal=event.getAuthentication().getPrincipal();
		if(principal instanceof String)
		{
			String username=(String)event.getAuthentication().getPrincipal();
			attemptService.addUserLoginAttemptCache(username);
		}
	}
}
