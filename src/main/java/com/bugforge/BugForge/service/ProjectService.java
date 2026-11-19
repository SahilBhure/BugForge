package com.bugforge.BugForge.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bugforge.BugForge.data.Project;
import com.bugforge.BugForge.data.ProjectMembership;
import com.bugforge.BugForge.data.ProjectRepository;
import com.bugforge.BugForge.data.ProjectRole;
import com.bugforge.BugForge.data.Users;
import com.bugforge.BugForge.data.UsersRepository;

@Service
public class ProjectService{
	
	private ProjectRepository projectRepository;
	private UsersRepository userRepository;
	

	public ProjectService(ProjectRepository projectRepository,UsersRepository usersRepository) {
		super();
		this.projectRepository = projectRepository;
		this.userRepository = usersRepository;
	}
	
	
	
	
	public List<Project> retrieveAllUserProjects() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String email = authentication.getName();
	    return projectRepository.findAllUserProjects(email);
	}
	
	
	public Project retrieveProjectById(Integer id) {
		return projectRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Project not found"));
		
	}
	
	
	public void addProject(Project project) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String email = authentication.getName();

	    Users owner = userRepository.findByMail(email)
	        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

	    project.setOwner(owner);

	    ProjectMembership membership = new ProjectMembership(owner, project, ProjectRole.ADMIN);
	    project.getMemberships().add(membership);

	    projectRepository.save(project);
	}
	
	

	public void deleteProjectFromUser(Integer id) {
	    projectRepository.deleteById(id);
		
	}
	
	

	public void updateProjectFromUser(Project project,Integer id) {
		Project tempPro = projectRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Project not found"));
		
		tempPro.setName(project.getName());
		tempPro.setDescription(project.getDescription());
		tempPro.setStartDate(project.getStartDate());
		tempPro.setEndDate(project.getEndDate());
		
		projectRepository.save(tempPro);
		
	}


	

	
	
}
