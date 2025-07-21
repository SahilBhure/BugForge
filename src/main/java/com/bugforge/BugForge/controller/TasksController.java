package com.bugforge.BugForge.controller;

import java.util.List;
import java.util.Optional;
import java.util.Objects;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bugforge.BugForge.data.Project;
import com.bugforge.BugForge.data.Tasks;
import com.bugforge.BugForge.service.ProjectService;
import com.bugforge.BugForge.service.TasksService;

import jakarta.transaction.Transactional;

@RestController
public class TasksController {
	
	private TasksService tasksService;

	public TasksController(TasksService tasksService) {
		super();
		this.tasksService = tasksService;
	}
	
	
	//Find all Tasks
	@GetMapping("/projects/tasks")
	public List<Tasks> retrieveAllTasks(){
		return tasksService.retrieveAllTasks();
	}
	
	//Find Task By Id
	@GetMapping("/projects/tasks/{id}")
	public Optional<Tasks> retrieveTaskById(@PathVariable Integer id){
		return tasksService.retrieveTaskById(id);
	}
	
	//Find All Tasks By Project Id
	@GetMapping("/projects/{projectId}/tasks")
	public List<Tasks> retrieveTasksByProjectId(@PathVariable Integer projectId) {
	    return tasksService.retrieveAllTasks().stream()
	            .filter(task -> task.getProject() != null && Objects.equals(task.getProject().getId(), projectId))
	            .toList();
	}
	
	
	//Add task
	@PostMapping("/projects/tasks")
	@Transactional
	public void addTask(@RequestBody Tasks tasks){
		tasksService.addTask(tasks);
	}
	
	//Delete Task
	@DeleteMapping("/projects/tasks/{id}")
	public void deleteTaskById(@PathVariable int id){
		tasksService.removeTaskById(id);
	}
	
	//Update Task
	@PutMapping("/projects/tasks/{id}")
	public void updateTasksById(@PathVariable int id, @RequestBody Tasks tasks) {
		tasksService.updateTaskById(id, tasks);
	}
	
	
}
