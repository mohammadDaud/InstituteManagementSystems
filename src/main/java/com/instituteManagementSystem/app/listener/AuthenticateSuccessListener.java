package com.instituteManagementSystem.app.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.instituteManagementSystem.app.entities.UserPrincipal;
import com.instituteManagementSystem.app.service.LoginAttemptService;

@Component
public class AuthenticateSuccessListener 
{
	private LoginAttemptService attemptService;

	@Autowired
	public AuthenticateSuccessListener(LoginAttemptService attemptService) 
	{
		this.attemptService = attemptService;
	}
	
	@EventListener
	public void onAuthenticateSuccess(AuthenticationSuccessEvent event)
	{
		Object principal=event.getAuthentication().getPrincipal();
		if(principal instanceof UserPrincipal)
		{
			UserPrincipal user=(UserPrincipal)event.getAuthentication().getPrincipal();
			attemptService.addUserLoginAttemptCache(user.getUsername());
		}
	}
}