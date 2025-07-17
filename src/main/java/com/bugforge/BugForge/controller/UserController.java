package com.bugforge.BugForge.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bugforge.BugForge.data.Users;
import com.bugforge.BugForge.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@RestController
public class UserController {
	
	private UserService userservice;
	
	public UserController(UserService userservice) {
		super();
		this.userservice = userservice;
	}
			

	
	//Root Page
	
	@GetMapping("/")
	public String hello(Authentication authentication) {
	    return userservice.HelloUser(authentication);
	}
	
	
	
	///Get,Add,Remove,Update User
	
	
	@GetMapping("/users")
	public Users getAllUsers(){
		return userservice.retrieveUser();
	}
	
	
	@PostMapping("/users")
	@Transactional
	public void addAUser(@RequestBody Users user){
		userservice.addAUser(user);
	}
	
	
	@DeleteMapping("/users")
	public void deleteUser(HttpServletRequest request, HttpServletResponse response,
	                       Authentication authentication) {
	    userservice.deleteUser(authentication);
	    new SecurityContextLogoutHandler().logout(request, response, authentication);
	}

	
	@PutMapping("/users")
	public void updateUser(@RequestBody Users user) {
		userservice.updateUser(user);
	}
	
	
}
