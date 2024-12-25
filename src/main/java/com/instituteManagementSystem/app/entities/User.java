package com.instituteManagementSystem.app.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
@Entity
public class User implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false,updatable = false)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Long id;
	@Column(name = "userId")
	private String userId;
	@Column(name = "firstName")
	private String firstName;
	@Column(name = "lastName")
	private String lastName;
	@Column(name = "userName")
	private String userName;
	@Column(name = "password")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	@Column(name = "email")
	private String email;
	@Column(name = "profileImageUrl")
	private String profileImageUrl;
	@Column(name = "lastLoginDate")
	private Date lastLoginDate;
	@Column(name = "lastLoginDateDisplay")
	private Date lastLoginDateDisplay;
	@Column(name = "joinDate")
	private Date joinDate;
	@Column(name = "role")
	private String role;
	@Column(name = "authorities")
	private String[] authorities;
	@Column(name = "isActive")
	private boolean isActive;
	@Column(name = "isNotlocked")
	private boolean isNotlocked;
	
	
	public User() {}
	public User(Long id, String userId, String firstName, String lastName, String userName, String password,
			String email, String profileImageUrl, Date lastLoginDate, Date lastLoginDateDisplay, Date joinDate,
			String role, String[] authorities, boolean isActive, boolean isNotlocked) 
	{
		this.id = id;
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.profileImageUrl = profileImageUrl;
		this.lastLoginDate = lastLoginDate;
		this.lastLoginDateDisplay = lastLoginDateDisplay;
		this.joinDate = joinDate;
		this.role = role;
		this.authorities = authorities;
		this.isActive = isActive;
		this.isNotlocked = isNotlocked;
	}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	public Date getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public Date getLastLoginDateDisplay() {
		return lastLoginDateDisplay;
	}
	public void setLastLoginDateDisplay(Date lastLoginDateDisplay) {
		this.lastLoginDateDisplay = lastLoginDateDisplay;
	}
	public Date getJoinDate() {
		return joinDate;
	}
	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String[] getAuthorities() {
		return authorities;
	}
	public void setAuthorities(String[] authorities) {
		this.authorities = authorities;
	}
	public boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	public boolean getIsNotlocked() {
		return isNotlocked;
	}
	public void setIsNotlocked(boolean isNotlocked) {
		this.isNotlocked = isNotlocked;
	}

	
}
