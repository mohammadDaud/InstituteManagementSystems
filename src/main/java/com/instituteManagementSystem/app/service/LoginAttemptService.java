package com.instituteManagementSystem.app.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class LoginAttemptService {
	private static final int MAXIMUM_NUMBER_OF_ATTEMPT_LOGIN = 5;
	private static final int ATTEMPT_INCREMENT = 1;
	private LoadingCache<String, Integer> loginAttempeCache;

	public LoginAttemptService() {
		super();
		loginAttempeCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).maximumSize(100)
				.build(new CacheLoader<String, Integer>() {
					public Integer load(String key) {
						return 0;
					}
				});
	}

	public void evictUserFromLoginAttemptCache(String Username) {
		loginAttempeCache.invalidate(Username);
	}

	public void addUserLoginAttemptCache(String Username) {
		int attempt = 0;
		try {
			attempt = ATTEMPT_INCREMENT + loginAttempeCache.get(Username);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		loginAttempeCache.put(Username, attempt);
	}

	public boolean hasExceededMaxAttempt(String Username) {
		try {
			return loginAttempeCache.get(Username) >= MAXIMUM_NUMBER_OF_ATTEMPT_LOGIN;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}
}
