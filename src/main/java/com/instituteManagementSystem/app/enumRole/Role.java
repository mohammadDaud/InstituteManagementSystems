package com.instituteManagementSystem.app.enumRole;
import com.instituteManagementSystem.app.constent.Authority;
public enum Role {
	ROLES_USER(Authority.USER_AUTHORITIES),
	ROLES_HR(Authority.HR_AUTHORITIES),
	ROLES_MANAGER(Authority.MANAGER_AUTHORITIES),
	ROLES_ADMIN(Authority.ADMIN_AUTHORITIES),
	ROLES_SUPER_ADMIN(Authority.SUPER_ADMIN_AUTHORITIES);

	private String[] authorities;

	Role(String... authorities)
	{
		this.authorities=authorities;
	}
	public String[] getAuthorities()
	{
		return authorities;
	}
}
