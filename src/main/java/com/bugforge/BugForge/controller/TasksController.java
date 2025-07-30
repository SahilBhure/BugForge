package com.bugforge.BugForge.controller;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bugforge.BugForge.data.Project;
import com.bugforge.BugForge.data.ProjectRole;
import com.bugforge.BugForge.data.Tasks;
import com.bugforge.BugForge.data.Users;
import com.bugforge.BugForge.service.ProjectAccessService;
import com.bugforge.BugForge.service.ProjectService;
import com.bugforge.BugForge.service.TasksService;
import com.bugforge.BugForge.service.UserService;

import jakarta.transaction.Transactional;

@RestController
public class TasksController {

    private final TasksService tasksService;

    public TasksController(TasksService tasksService) {
        this.tasksService = tasksService;
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<?> getTasks(@PathVariable Integer projectId, Principal principal) {
        return tasksService.getTasksForProject(projectId, principal);
    }

    @PostMapping("/projects/tasks")
    @Transactional
    public ResponseEntity<?> createTask(@RequestBody Tasks task, Principal principal) {
        return tasksService.createTask(task, principal);
    }

    @PutMapping("/projects/tasks/{id}")
    public ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody Tasks task, Principal principal) {
        return tasksService.updateTask(id, task, principal);
    }

    @DeleteMapping("/projects/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable int id, Principal principal) {
        return tasksService.deleteTask(id, principal);
    }
}
