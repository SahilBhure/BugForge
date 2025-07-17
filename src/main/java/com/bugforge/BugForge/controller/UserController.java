package com.bugforge.BugForge.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bugforge.BugForge.data.Users;
import com.bugforge.BugForge.service.UserService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
public class UserController {
	
	private UserService userservice;
	
	public UserController(UserService userservice) {
		super();
		this.userservice = userservice;
	}
			
	
	

	@GetMapping("/")
	public String hello(Authentication authentication) {
	    return userservice.HelloUser(authentication);
	}

	
	
	@GetMapping("/users")
	public Users getAllUsers(){
		return userservice.retrieveUser();
	}
	
	
	@PostMapping("/users")
	@Transactional
	public void addAUser(@RequestBody Users user){
		userservice.addAUser(user);
	}
	
}
