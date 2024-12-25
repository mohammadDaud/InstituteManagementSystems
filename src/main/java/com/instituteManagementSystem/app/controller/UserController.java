package com.instituteManagementSystem.app.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.instituteManagementSystem.app.constent.FileConstents;
import com.instituteManagementSystem.app.constent.SecurityConstents;
import com.instituteManagementSystem.app.entities.HttpResponse;
import com.instituteManagementSystem.app.entities.User;
import com.instituteManagementSystem.app.entities.UserPrincipal;
import com.instituteManagementSystem.app.exceptions.EmailExistException;
import com.instituteManagementSystem.app.exceptions.EmailNotFoundException;
import com.instituteManagementSystem.app.exceptions.ExceptionHandling;
import com.instituteManagementSystem.app.exceptions.UserNotFoundException;
import com.instituteManagementSystem.app.exceptions.UsernameExistException;
import com.instituteManagementSystem.app.service.UserService;
import com.instituteManagementSystem.app.utility.JWTTokenProvider;

@RestController
@RequestMapping(path = { "/", "/user"})
public class UserController extends ExceptionHandling{
	private UserService userService;
	private AuthenticationManager authManager;
	private JWTTokenProvider jwtTPro;

	@Autowired
	public UserController(UserService userService,
			AuthenticationManager authManager,
			JWTTokenProvider jwtTPro) 
	{
		this.userService =userService;
		this.authManager=authManager;
		this.jwtTPro=jwtTPro;
	}

	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody User user)

	{
		authenticate(user.getUserName(),user.getPassword());
		User loginuser=userService.findUserByUsername(user.getUserName());
		UserPrincipal principal=new UserPrincipal(loginuser);
		HttpHeaders jwtheader=getJwtHeader(principal);
		return new ResponseEntity<>(loginuser,jwtheader,HttpStatus.OK);

	}


	@PostMapping("/register")
	public ResponseEntity<User> getRegister(@RequestBody User user)
			throws UserNotFoundException, EmailExistException, UsernameExistException
	{
		User newUser=userService.register(user.getFirstName(), user.getLastName(), user.getUserName(), user.getEmail());
		return new ResponseEntity<>(newUser,HttpStatus.OK);

	}

	@PostMapping("/add")
	public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName,
			@RequestParam("username") String username,
			@RequestParam("email") String email,
			@RequestParam("role") String role,
			@RequestParam("isActive") String isActive,
			@RequestParam("isNotLocked") String isNotLocked,
			@RequestParam(value = "profileImage",required = false) MultipartFile profileImage)
	{
		User newUser=userService.addNewUser(firstName, lastName, username, email, role
				,Boolean.parseBoolean(isNotLocked), Boolean.parseBoolean(isActive), profileImage);
		return new ResponseEntity<>(newUser,HttpStatus.OK);
	}

	@PostMapping("/update")
	public ResponseEntity<User> updateUser(@RequestParam("currentUsername") String currentUsername,
			@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName,
			@RequestParam("username") String username,
			@RequestParam("email") String email,
			@RequestParam("role") String role,
			@RequestParam("isActive") String isActive,
			@RequestParam("isNotLocked") String isNotLocked,
			@RequestParam(value = "profileImage",required = false) MultipartFile profileImage)
	{
		User updateUser=userService.updateUser(currentUsername,firstName, lastName, username, email, role
				,Boolean.parseBoolean(isNotLocked), Boolean.parseBoolean(isActive), profileImage);
		return new ResponseEntity<>(updateUser,HttpStatus.OK);
	}

	@GetMapping("/find/{username}")
	public ResponseEntity<User> getUser(@PathVariable("username") String username)
	{
		User user=userService.findUserByUsername(username);
		return new ResponseEntity<User>(user,HttpStatus.OK);
	}

	@GetMapping("/list")
	public ResponseEntity<List<User>> getAllUser()
	{
		List<User> userlist=userService.getAllUsers();
		return new ResponseEntity<List<User>>(userlist,HttpStatus.OK);
	}

	@GetMapping("/restpassword/{email}")
	public ResponseEntity<HttpResponse> restUserPassword(@PathVariable("email") String email) throws EmailNotFoundException
	{
		userService.restUserPassword(email);
		return responseUser(HttpStatus.OK,"An email with a new password was send to:"+email);
	}

	@DeleteMapping("/delete/{username}")
	@PreAuthorize("hasAnyAuthority('user:delete')")
	public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username)
	{
		userService.deleteUser(username);
		return responseUser(HttpStatus.OK,"user delete successfully!");
	}

	@PostMapping("/updateProfileImage")
	public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username,
			@RequestParam(value = "profileImage") MultipartFile profileImage)
	{
		User updateUserProfileImage=userService.updateUserImage(username, profileImage);
		return new ResponseEntity<>(updateUserProfileImage,HttpStatus.OK);
	}

	@GetMapping(path = "/image/{username}/{fileName}",produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getProfileImage(@PathVariable("username") String username,@PathVariable("fileName") String fileName)
	{
		try {
			byte[] byteImage = Files.readAllBytes(Paths.get(FileConstents.USER_FOLDER +
					username + 
					FileConstents.FORWARD_SLASH + fileName ));
			return byteImage;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@GetMapping(path = "/image/profile/{username}",produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getTempProfileImage(@PathVariable("username") String username)
	{
		try {
			URL url=new URL(FileConstents.TEMP_PROFILE_IMAGE_BASE_URL +username);
			ByteArrayOutputStream arrayOutputStream=new ByteArrayOutputStream();
			try(InputStream  inputStream=url.openStream())
			{
				int bytesRead;
				byte[] chunk=new byte[1024];
				while((bytesRead=inputStream.read(chunk))>0)
				{
					arrayOutputStream.write(chunk,0, bytesRead);
				}
			}
			return arrayOutputStream.toByteArray();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return null;
		}

	}

	private ResponseEntity<HttpResponse> responseUser(HttpStatus httpStatus, String message) {
		HttpResponse body=new HttpResponse(httpStatus.value(),
				httpStatus,
				httpStatus.getReasonPhrase().toUpperCase(),
				message.toUpperCase());
		return new ResponseEntity<>(body,httpStatus);
	}

	private HttpHeaders getJwtHeader(UserPrincipal principal) {
		HttpHeaders header=new HttpHeaders();
		header.add(SecurityConstents.JWT_TOKEN_HEADER, jwtTPro.generateJWTToken(principal));
		return header;
	}

	private void authenticate(String userName, String password) {
		authManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));

	}
}

