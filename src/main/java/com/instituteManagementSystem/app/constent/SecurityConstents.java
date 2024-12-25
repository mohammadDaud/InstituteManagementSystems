package com.instituteManagementSystem.app.constent;

public class SecurityConstents {
	public static final long EXIRAING_TIME =432_000_000;//1 Day
	public static final String TOKEN_PREFIX ="Bearer ";
	public static final String JWT_TOKEN_HEADER ="Jwt-Token";
	public static final String TOKEN_CANNOT_BE_VERIFIED ="Token cannot be verified";
	public static final String IMS_LLC ="IMS,LLC";
	public static final String IMS_ADMINISTRATION ="Ims Portal";
	public static final String AUTHORITIES ="authorities";
	public static final String FORBIDDEN_MESSAGE ="You need to log in to access this page";
	public static final String ACCESS_DENIED_MESSAGE ="You do not have to permission to access this page";
	public static final String OPTIONS_HTTP_METHOD ="OPTIONS";
	public static final String[] PUBLIC_URLS = { "/user/login", "/user/register","/user/image/**" };
}
