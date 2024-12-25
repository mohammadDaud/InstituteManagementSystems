package com.instituteManagementSystem.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.instituteManagementSystem.app.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{
	public User findByUserName(String username);
	public User findByEmail(String email);
}
