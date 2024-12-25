package com.instituteManagementSystem.app.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.instituteManagementSystem.app.constent.FileConstents;
import com.instituteManagementSystem.app.entities.User;
import com.instituteManagementSystem.app.entities.UserPrincipal;
import com.instituteManagementSystem.app.enumRole.Role;
import com.instituteManagementSystem.app.exceptions.EmailExistException;
import com.instituteManagementSystem.app.exceptions.EmailNotFoundException;
import com.instituteManagementSystem.app.exceptions.NotAnImageFileException;
import com.instituteManagementSystem.app.exceptions.UserNotFoundException;
import com.instituteManagementSystem.app.exceptions.UsernameExistException;
import com.instituteManagementSystem.app.repository.UserRepository;
import com.instituteManagementSystem.app.service.EmailService;
import com.instituteManagementSystem.app.service.LoginAttemptService;
import com.instituteManagementSystem.app.service.UserService;



@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService,UserDetailsService {
	private final Logger logger=LoggerFactory.getLogger(UserServiceImpl.class);
	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;
	private LoginAttemptService loginAttemptService;
	private EmailService emailService;

	@Autowired
	public UserServiceImpl(UserRepository userRepository,
			BCryptPasswordEncoder passwordEncoder,
			LoginAttemptService loginAttemptService,
			EmailService emailService) {
		this.userRepository = userRepository;
		this.passwordEncoder=passwordEncoder;
		this.loginAttemptService=loginAttemptService;
		this.emailService=emailService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user=userRepository.findByUserName(username);
		if(user==null)
		{
			logger.error("User not found by username:"+username);
			throw new UsernameNotFoundException("User not found by username:"+username);
		}
		else
		{
			validateLoginUser(user);
			user.setLastLoginDateDisplay(user.getLastLoginDate());
			user.setLastLoginDate(new Date());
			userRepository.save(user);
			UserPrincipal principal=new UserPrincipal(user);
			logger.info("User found By username:"+username);
			return principal;
		}
	}

	private void validateLoginUser(User user) {
		if(user.getIsNotlocked())
		{
			if(loginAttemptService.hasExceededMaxAttempt(user.getUserName()))
			{
				user.setIsNotlocked(false);
			}
			else
			{
				user.setIsNotlocked(true);
			}
		}
		else
		{
			loginAttemptService.evictUserFromLoginAttemptCache(user.getUserName());
		}

	}

	@Override
	public User register(String firstName, String lastName, String username, String email) 
			throws EmailExistException, UserNotFoundException, UsernameExistException 
	{
		validateUsernameAndEmail(StringUtils.EMPTY,username,email);
		User user=new User();
		user.setUserId(generateId());
		String password=generatePassword();
		String encodedPassword=generateEncodedPassword(password);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUserName(username);
		user.setPassword(encodedPassword);
		user.setEmail(email);
		user.setJoinDate(new Date());
		user.setIsActive(true);
		user.setIsNotlocked(true);
		user.setRole(Role.ROLES_USER.name());
		user.setAuthorities(Role.ROLES_USER.getAuthorities());
		user.setProfileImageUrl(getTemporaryImageUrl(username));
		userRepository.save(user);
		//======emailSending To New User====
		/*
		 * Gmail considers regular email programs (like the one we're building) to be
		 * "less secure", so in order for our app to get access into your account, your
		 * "Allow less secure apps" option must be turned on on your Gmail account.
		 * 
		 * Please see link below:
		 * 
		 * https://hotter.io/docs/email-accounts/secure-app-gmail/
		 * 
		 * If you don't allow sign-ins from less secure apps, the email won't be sent as
		 * the call to connect to your Gmail account will fail.
		 */
		emailService.sendNewPasswordEmail(firstName, password,email);
		logger.info("User Password:======>>"+password);
		return user;
	}
	
	@Override
	public User addNewUser(String firstName, String lastName, String username, String email, String role,
			boolean isNotLocked, boolean isActive, MultipartFile profileImage) {
		try 
		{
			validateUsernameAndEmail(StringUtils.EMPTY, username, email);
			User user=new User();
			user.setUserId(generateId());
			String password=generatePassword();
			String encodedPassword=generateEncodedPassword(password);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setUserName(username);
			user.setPassword(encodedPassword);
			user.setEmail(email);
			user.setJoinDate(new Date());
			user.setIsActive(isActive);
			user.setIsNotlocked(isNotLocked);
			user.setRole(getRoleEnumName(role).name());
			user.setAuthorities(getRoleEnumName(role).getAuthorities());
			user.setProfileImageUrl(getTemporaryImageUrl(username));
			userRepository.save(user);
			//======emailSending To New User====
			/*
			 * Gmail considers regular email programs (like the one we're building) to be
			 * "less secure", so in order for our app to get access into your account, your
			 * "Allow less secure apps" option must be turned on on your Gmail account.
			 * 
			 * Please see link below:
			 * 
			 * https://hotter.io/docs/email-accounts/secure-app-gmail/
			 * 
			 * If you don't allow sign-ins from less secure apps, the email won't be sent as
			 * the call to connect to your Gmail account will fail.
			 */
			//emailService.sendNewPasswordEmail(firstName, password,email);
			try {
				saveProfileImage(user,profileImage);
			} catch (NotAnImageFileException e) {
				e.printStackTrace();
			}
			logger.info("User Password:======>>"+password);
			return user;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername,
			String newEmail, String newRole, boolean isNotLocked, boolean isActive, MultipartFile profileImage) {
		User currentUser;
		try {
			currentUser = validateUsernameAndEmail(currentUsername, newUsername, newEmail);
			currentUser.setFirstName(newFirstName);
			currentUser.setLastName(newLastName);
			currentUser.setUserName(newUsername);
			currentUser.setEmail(newEmail);
			currentUser.setIsActive(isActive);
			currentUser.setIsNotlocked(isNotLocked);
			currentUser.setRole(getRoleEnumName(newRole).name());
			currentUser.setAuthorities(getRoleEnumName(newRole).getAuthorities());
			userRepository.save(currentUser);
			try {
				saveProfileImage(currentUser,profileImage);
			} catch (NotAnImageFileException e) {
				e.printStackTrace();
			}
			return currentUser;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		} 
	}
	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public User findUserByUsername(String username) {
		return userRepository.findByUserName(username);
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public void deleteUser(String username) {
		User user=userRepository.findByUserName(username);
		Path userFolder=Paths.get(FileConstents.USER_FOLDER + user.getUserName()).toAbsolutePath().normalize();
		try {
			FileUtils.deleteDirectory(new File(userFolder.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		userRepository.deleteById(user.getId());
	}

	@Override
	public void restUserPassword(String email) throws EmailNotFoundException {
		User user=userRepository.findByEmail(email);
		if(user==null)
		{
			throw new EmailNotFoundException("No user found by Email:"+email);
		}
		String password=generatePassword();
		logger.info("NEW PASSWORD====>>> "+password);
		user.setPassword(generateEncodedPassword(password));
		userRepository.save(user);
		emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
	}

	@Override
	public User updateUserImage(String username, MultipartFile profileImage) {
		try 
		{
			User user=validateUsernameAndEmail(username, null, null);
			try {
				saveProfileImage(user, profileImage);
			} catch (NotAnImageFileException e) {
				e.printStackTrace();
			}
			return user;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}

	private void saveProfileImage(User user, MultipartFile profileImage) throws NotAnImageFileException 
	{
		if(profileImage!=null)
		{
			if(!Arrays.asList(MediaType.IMAGE_JPEG_VALUE,
							  MediaType.IMAGE_PNG_VALUE,
							  MediaType.IMAGE_GIF_VALUE).contains(profileImage))
			{
				throw new NotAnImageFileException(profileImage.getOriginalFilename()+ "is not an image file!");
			}
			Path userFolder=Paths.get(FileConstents.USER_FOLDER +user.getUserName()).toAbsolutePath().normalize();
			if(Files.exists(userFolder))
			{
				try 
				{
					Files.createDirectories(userFolder);
					logger.info(FileConstents.DIRECTORY_CREATED + userFolder);
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			else
			{
				try 
				{
					Files.deleteIfExists(Paths.get(userFolder + user.getUserName() + FileConstents.DOT + FileConstents.JPG_EXTENTION));
					Files.copy(profileImage.getInputStream(), 
							userFolder.resolve(user.getUserName() + FileConstents.DOT + FileConstents.JPG_EXTENTION),
							StandardCopyOption.REPLACE_EXISTING);
					user.setProfileImageUrl(setProfileImageUrl(user.getUserName()));
					userRepository.save(user);
					logger.info(FileConstents.FILE_SAVED_IN_FILE_SYSTEM +profileImage.getOriginalFilename());
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			
		    }
	   }
    }

	private String setProfileImageUrl(String userName) {
		return ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(FileConstents.USER_IMAGE_PATH + userName + 
						FileConstents.FORWARD_SLASH + userName + 
						FileConstents.DOT + FileConstents.JPG_EXTENTION).toUriString();
	}

	private Role getRoleEnumName(String role) {
		return Role.valueOf(role.toUpperCase());
	}

	private String getTemporaryImageUrl(String username) {
		return ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(FileConstents.DEFAULT_USER_IMAGE_PATH + username).toUriString();
	}

	private String generateEncodedPassword(String password) {
		return passwordEncoder.encode(password);
	}

	private String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(10);
	}

	private String generateId() {
		return RandomStringUtils.randomNumeric(10);
	}

	private User validateUsernameAndEmail(String currentUsername,String newUsername,String newEmail) 
			throws UserNotFoundException, UsernameExistException, EmailExistException 
			 {
		User newUser=findUserByUsername(newUsername);
		User byNewEmail=findUserByEmail(newEmail);
		if(StringUtils.isNoneBlank(currentUsername))
		{
			User currentUser=findUserByUsername(currentUsername);
			if(currentUser==null)
			{
				throw new UserNotFoundException("No user found  by username "+currentUsername);
			}

			if(newUser!=null && !currentUser.getId().equals(newUser.getId()))
			{
				throw new UsernameExistException("Username already exists ");
			}

			if(byNewEmail!=null && !currentUser.getId().equals(byNewEmail.getId()))
			{
				throw new EmailExistException("This email already exists ");
			}
			return currentUser;
		}
		else
		{
			if(newUser!=null)
			{
				throw new UsernameExistException("Username already exists ");
			}
			if(byNewEmail!=null)
			{
				throw new EmailExistException("This email already exists ");
			}
			return null;
		}

	}
}
