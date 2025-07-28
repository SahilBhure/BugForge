package com.bugforge.BugForge.service;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bugforge.BugForge.data.Users;
import com.bugforge.BugForge.data.UsersRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	private UsersRepository userRepository;
	
	public CustomUserDetailsService(UsersRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}



	@Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepository.findByMail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getMail())
                .password(user.getPassword())
                .authorities(user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role))
                		.collect(Collectors.toList()))
                .build();
    }

}
