package com.bugforge.BugForge.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bugforge.BugForge.data.Project;
import com.bugforge.BugForge.service.ProjectService;

import jakarta.transaction.Transactional;

@RestController
public class ProjectController {
	
	private ProjectService projectService;
	
	
	public ProjectController(ProjectService projectService) {
		super();
		this.projectService = projectService;
	}

	
	@GetMapping("/user/projects")
	@PreAuthorize("hasRole('ADMIN')")
	public List<Project> retrieveAllUserProjects(){
		return projectService.retrieveAllUserProjects();
	}
	
	@GetMapping("/user/projects/{id}")
	public Project retrieveProjectById(@PathVariable Integer id){
		return projectService.retrieveProjectById(id);
	}
	
	@PostMapping("/user/projects")
	@Transactional
	public void addProject(@RequestBody Project project){
		projectService.addProject(project);
	}
	
	@DeleteMapping("/user/projects/{id}")
	public void deleteProjectFromUser(@PathVariable Integer id){
		projectService.deleteProjectFromUser(id);
	}
	
	@PutMapping("/user/projects/{id}")
	public void updateProjectFromUser(@RequestBody Project project, @PathVariable Integer id){
		projectService.updateProjectFromUser(project,id);
	}
	
}
