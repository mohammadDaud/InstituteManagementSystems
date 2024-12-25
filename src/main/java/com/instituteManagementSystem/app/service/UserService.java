package com.instituteManagementSystem.app.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.instituteManagementSystem.app.entities.User;
import com.instituteManagementSystem.app.exceptions.EmailExistException;
import com.instituteManagementSystem.app.exceptions.EmailNotFoundException;
import com.instituteManagementSystem.app.exceptions.UserNotFoundException;
import com.instituteManagementSystem.app.exceptions.UsernameExistException;

public interface UserService 
{
	User register(String firstName,String lastName,String username,String email) 
			throws EmailExistException, UserNotFoundException, UsernameExistException;
	
	List<User> getAllUsers();
	
	User findUserByUsername(String username);
	
	User findUserByEmail(String email);
	
	User addNewUser(String firstName,String lastName,String username,String email,String role,boolean isNotLocked,boolean isActive,MultipartFile profileImage);

	User updateUser(String currentUsername,String newFirstName,String newLastName,String newUsername,String newEmail,String newRole,boolean isNotLocked,boolean isActive,MultipartFile profileImage);
    
	void deleteUser(String username);
	
	void restUserPassword(String email) throws EmailNotFoundException;
	
	User updateUserImage(String username,MultipartFile profileImage);
}
