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
    private final ProjectAccessService accessService;
    private final ProjectService projectService;
    private final UserService userService;

    public TasksController(
        TasksService tasksService,
        ProjectAccessService accessService,
        ProjectService projectService,
        UserService userService
    ) {
        this.tasksService = tasksService;
        this.accessService = accessService;
        this.projectService = projectService;
        this.userService = userService;
    }

    // 🔍 Get All Tasks in a Project (read access)
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<?> retrieveTasksByProjectId(@PathVariable Integer projectId, Principal principal) {
        Users user = userService.getByMail(principal.getName());
        Project project = projectService.retrieveProjectById(projectId);

        // Optionally check if member
        if (!accessService.hasRole(user, project, ProjectRole.ADMIN, ProjectRole.MANAGER, ProjectRole.DEVELOPER, ProjectRole.TESTER, ProjectRole.VIEWER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        List<Tasks> tasks = tasksService.retrieveAllTasks().stream()
                .filter(task -> task.getProject() != null && Objects.equals(task.getProject().getId(), projectId))
                .toList();

        return ResponseEntity.ok(tasks);
    }

    // ✅ Create Task (write permission)
    @PostMapping("/projects/tasks")
    @Transactional
    public ResponseEntity<?> addTask(@RequestBody Tasks tasks, Principal principal) {
        Users user = userService.getByMail(principal.getName());
        Project project = tasks.getProject();

        if (project == null) {
            return ResponseEntity.badRequest().body("Project is required");
        }

        if (!accessService.hasRole(user, project, ProjectRole.ADMIN, ProjectRole.MANAGER, ProjectRole.DEVELOPER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient permissions");
        }

        tasksService.addTask(tasks);
        return ResponseEntity.ok("Task created");
    }

    // ✅ Update Task
    @PutMapping("/projects/tasks/{id}")
    public ResponseEntity<?> updateTasksById(@PathVariable int id, @RequestBody Tasks updatedTask, Principal principal) {
        Tasks existing = tasksService.retrieveTaskById(id).orElse(null);
        if (existing == null) return ResponseEntity.notFound().build();

        Users user = userService.getByMail(principal.getName());
        Project project = existing.getProject();

        if (!accessService.hasRole(user, project, ProjectRole.ADMIN, ProjectRole.MANAGER, ProjectRole.DEVELOPER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient permissions");
        }

        tasksService.updateTaskById(id, updatedTask);
        return ResponseEntity.ok("Task updated");
    }

    // ✅ Delete Task
    @DeleteMapping("/projects/tasks/{id}")
    public ResponseEntity<?> deleteTaskById(@PathVariable int id, Principal principal) {
        Tasks task = tasksService.retrieveTaskById(id).orElse(null);
        if (task == null) return ResponseEntity.notFound().build();

        Users user = userService.getByMail(principal.getName());
        Project project = task.getProject();

        if (!accessService.hasRole(user, project, ProjectRole.ADMIN, ProjectRole.MANAGER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient permissions");
        }

        tasksService.removeTaskById(id);
        return ResponseEntity.ok("Task deleted");
    }
}
