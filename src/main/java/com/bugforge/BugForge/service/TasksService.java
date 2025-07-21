package com.bugforge.BugForge.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bugforge.BugForge.data.Project;
import com.bugforge.BugForge.data.ProjectRepository;
import com.bugforge.BugForge.data.Tasks;
import com.bugforge.BugForge.data.TasksRepository;
import com.bugforge.BugForge.data.UsersRepository;

@Service
public class TasksService{

	private TasksRepository tasksRepository;

	public TasksService(TasksRepository tasksRepository) {
		super();
		this.tasksRepository = tasksRepository;
	}
	
	
	public List<Tasks> retrieveAllTasks(){
		return tasksRepository.findAll();
	}

	public Optional<Tasks> retrieveTaskById(int id) {
		return tasksRepository.findById(id);
	}
	

	public void addTask(Tasks tasks) {
		tasksRepository.save(tasks);
	}
	
	
	public void removeTaskById(int id) {
		tasksRepository.deleteById(id);
	}
	
	public void updateTaskById(int id,Tasks tasks) {
		Tasks tempTasks = tasksRepository.findById(id)
				.orElseThrow(() -> new UsernameNotFoundException("Task not found with id: " + id));
		tempTasks.setTitle(tasks.getTitle());
		tempTasks.setType(tasks.getType());
		tempTasks.setStatus(tasks.getStatus());
		tempTasks.setDescription(tasks.getDescription());
		tempTasks.setPriority(tasks.getPriority());
		tempTasks.setAssignee(tasks.getAssignee());
		tempTasks.setReporter(tasks.getReporter());
		tempTasks.setProject(tasks.getProject());
		tasksRepository.save(tempTasks);
	}
	
}
