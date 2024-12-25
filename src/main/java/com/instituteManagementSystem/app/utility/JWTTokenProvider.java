package com.instituteManagementSystem.app.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.instituteManagementSystem.app.constent.SecurityConstents;
import com.instituteManagementSystem.app.entities.UserPrincipal;

@Component
public class JWTTokenProvider {

	@Value("${jwt.secret}")
	private String secret;
	
	public String generateJWTToken(UserPrincipal principal)
	{
		String[] claims=getClaimsfromUser(principal);
		return JWT.create().withIssuer(SecurityConstents.IMS_LLC)
				.withAudience(SecurityConstents.IMS_ADMINISTRATION)
				.withIssuedAt(new Date()).withSubject(principal.getUsername())
				.withArrayClaim(SecurityConstents.AUTHORITIES, claims)
				.withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstents.EXIRAING_TIME))
				.sign(Algorithm.HMAC512(secret.getBytes()));
	}

	public List<GrantedAuthority> getAuthorities(String token)
	{
		//List<GrantedAuthority> ls=new ArrayList<>();
		String[] claim=getClaimFromToken(token);
		/*
		 * for(String s:claim) { ls.add(new SimpleGrantedAuthority(s)); } return ls;
		 */
	        return Arrays.stream(claim).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}
	public Authentication getAuthentication(String username,List<GrantedAuthority> authorities,HttpServletRequest request)
	{
		UsernamePasswordAuthenticationToken authenticationToken=new 
				UsernamePasswordAuthenticationToken(username, null,authorities);
		authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		return authenticationToken;
	}
	
	public boolean isTokenValid(String username,String token)
	{
		JWTVerifier verifier=getTokenVerification();
		return StringUtils.isNoneEmpty(username) && !isTokenExpired(verifier,token);
	}
	public String getSubject(String token) {
		JWTVerifier verifier=getTokenVerification();
		return verifier.verify(token).getSubject();
	}
	
	
	private boolean isTokenExpired(JWTVerifier verifier, String token) {
		Date expired=verifier.verify(token).getExpiresAt();
		return expired.before(new Date());
	}

	private String[] getClaimFromToken(String token) {
		JWTVerifier verifier=getTokenVerification();
		return verifier.verify(token).getClaim(SecurityConstents.AUTHORITIES).asArray(String.class);
	}

	private JWTVerifier getTokenVerification() {
		JWTVerifier verifier;
		try {
			Algorithm algorithm=Algorithm.HMAC512(secret);
			verifier=JWT.require(algorithm).withIssuer(SecurityConstents.IMS_LLC).build();
		} catch (JWTVerificationException e) {
			throw new JWTVerificationException(SecurityConstents.TOKEN_CANNOT_BE_VERIFIED);
		}
		return verifier;
	}

	private String[] getClaimsfromUser(UserPrincipal user) {
		List<String> authority=new ArrayList<>();
		for(GrantedAuthority autho : user.getAuthorities())
		{
			authority.add(autho.getAuthority());
		}
		return authority.toArray(new String[0]);
	}
}
