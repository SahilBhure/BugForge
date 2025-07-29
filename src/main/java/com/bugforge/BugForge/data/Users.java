package com.bugforge.BugForge.data;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;
	
	@NotBlank
	@Size(min = 5,max = 12)
	String username;
	
	@NotBlank
	@Email
	String mail;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	@NotBlank
	String password;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<ProjectMembership> memberships = new ArrayList<>();

	
	public Users() {}
	
	public Users(int id, String username, String mail, String password) {
		super();
		this.id = id;
		this.username = username;
		this.mail = mail;
		this.password = password;
	}
	
	 // ✅ ADD THIS: A field to store user roles.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    private Set<String> roles = new HashSet<>();
	
	// --- Existing constructors, getters, and setters ---
    
    // ✅ ADD THIS: Getter and setter for roles
    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Users [id=" + id + ", username=" + username + ", mail=" + mail + ", password=" + password + "]";
	}
	
	
	
	
}
