package com.bugforge.BugForge.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.bugforge.BugForge.data.Users;
import com.bugforge.BugForge.data.UsersRepository;

import jakarta.validation.Valid;

@Service
public class UserService{
	
	private UsersRepository usersRepository;
	private PasswordEncoder passwordEncoder;

	public UserService(UsersRepository usersRepository,PasswordEncoder passwordEncoder) {
		super();
		this.usersRepository = usersRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	
	
	
	public String HelloUser(Authentication authentication) {
		Object principal = authentication.getPrincipal();

	    if (principal instanceof OAuth2User oauth2User) {
	        return "Hello user: " + oauth2User.getAttribute("name");
	    } else if (principal instanceof UserDetails userDetails) {
	        return "Hello user: " + userDetails.getUsername();
	    } else {
	        return "Hello";
	    }
	}
	
	
	public List<Users> findAllUsers() {
		return usersRepository.findAll();
	}
	
	public Users retrieveUser() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String mail = authentication.getName(); 
	    return usersRepository.findByMail(mail)
	        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + mail));
	}
	
	
	public void addAUser(@Valid Users users) {
	    String hashedPassword = passwordEncoder.encode(users.getPassword());
	    users.setPassword(hashedPassword);
		usersRepository.save(users);
	}
	
}
