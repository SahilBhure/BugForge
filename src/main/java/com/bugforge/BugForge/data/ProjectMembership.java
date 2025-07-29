package com.bugforge.BugForge.data;

import jakarta.persistence.*;

@Entity
public class ProjectMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Enumerated(EnumType.STRING)
    private ProjectRole role;

    public ProjectMembership() {}

    public ProjectMembership(Users user, Project project, ProjectRole role) {
        this.user = user;
        this.project = project;
        this.role = role;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public ProjectRole getRole() {
		return role;
	}

	public void setRole(ProjectRole role) {
		this.role = role;
	}

	public ProjectMembership(Long id, Users user, Project project, ProjectRole role) {
		super();
		this.id = id;
		this.user = user;
		this.project = project;
		this.role = role;
	}

	@Override
	public String toString() {
		return "ProjectMembership [id=" + id + ", user=" + user + ", project=" + project + ", role=" + role + "]";
	}

    
    // Getters and setters
}
