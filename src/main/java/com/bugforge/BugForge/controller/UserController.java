package com.bugforge.BugForge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bugforge.BugForge.data.Users;
import com.bugforge.BugForge.service.JwtService;
import com.bugforge.BugForge.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@RestController
public class UserController {
	
	private UserService userservice;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtService jwtService;
	
	
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
	@PreAuthorize("hasRole('ADMIN')")
	public List<Users> getAllUsers(){
		return userservice.findAllUsers();
	}
	
	@GetMapping("/user")
	public Users getUser(){
		return userservice.retrieveUser();
	}
	
	
	@PostMapping("/registeruser")
	@Transactional
	public void addAUser(@RequestBody Users user){
		userservice.addAUser(user);
	}
	
	
	@PostMapping("/login")
	public String login(@RequestBody Users user) {
		
		Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getMail(), user.getPassword()));
		
		if(auth.isAuthenticated()) {
			  UserDetails userDetails = (UserDetails) auth.getPrincipal();
		      return jwtService.generateToken(userDetails); 
		}else {
			return "Failed";
		}
		
	}
	
	
	
	
	@DeleteMapping("/user")
	public void deleteUser(HttpServletRequest request, HttpServletResponse response,
	                       Authentication authentication) {
	    userservice.deleteUser(authentication);
	    new SecurityContextLogoutHandler().logout(request, response, authentication);
	}

	
	@PutMapping("/user")
	public void updateUser(@RequestBody Users user) {
		userservice.updateUser(user);
	}
	
	
}
