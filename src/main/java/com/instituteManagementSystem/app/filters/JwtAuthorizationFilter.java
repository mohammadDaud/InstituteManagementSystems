package com.instituteManagementSystem.app.filters;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.instituteManagementSystem.app.constent.SecurityConstents;
import com.instituteManagementSystem.app.utility.JWTTokenProvider;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter{

	private JWTTokenProvider jwttokenprovider;
	
	public JwtAuthorizationFilter(JWTTokenProvider jwttokenprovider) 
	{
		this.jwttokenprovider = jwttokenprovider;
	}
	
	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase(SecurityConstents.OPTIONS_HTTP_METHOD)) {
            response.setStatus(OK.value());
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.startsWith(SecurityConstents.TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authorizationHeader.substring(SecurityConstents.TOKEN_PREFIX.length());
            String username = jwttokenprovider.getSubject(token);
            if (jwttokenprovider.isTokenValid(username, token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorities = jwttokenprovider.getAuthorities(token);
                Authentication authentication = jwttokenprovider.getAuthentication(username, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

	/*
	 * @Override protected void doFilterInternal(HttpServletRequest request,
	 * HttpServletResponse response, FilterChain filterChain) throws
	 * ServletException, IOException {
	 * if(request.getMethod().equalsIgnoreCase(SecurityConstents.OPTIONS_HTTP_METHOD
	 * )) { response.setStatus(HttpStatus.OK.value()); } else { String
	 * authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
	 * logger.info("authorizationHeader==>"+authorizationHeader);
	 * if(authorizationHeader==null ||
	 * !authorizationHeader.startsWith(SecurityConstents.TOKEN_PREFIX)) {
	 * filterChain.doFilter(request, response); return; } String
	 * token=authorizationHeader.substring(SecurityConstents.TOKEN_PREFIX.length());
	 * logger.error("token====>"+token); String
	 * username=jwttokenprovider.getSubject(token);
	 * if(jwttokenprovider.isTokenValid(username, token)&&
	 * SecurityContextHolder.getContext().getAuthentication()==null) {
	 * List<GrantedAuthority> authority=jwttokenprovider.getAuthorities(token);
	 * Authentication authentication=jwttokenprovider.getAuthentication(username,
	 * authority, request);
	 * SecurityContextHolder.getContext().setAuthentication(authentication); } else
	 * { SecurityContextHolder.clearContext(); } } filterChain.doFilter(request,
	 * response); }
	 */
}