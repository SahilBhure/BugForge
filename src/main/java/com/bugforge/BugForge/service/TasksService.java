package com.bugforge.BugForge.service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bugforge.BugForge.data.*;

@Service
public class TasksService {

    private final TasksRepository tasksRepository;
    private final ProjectAccessService accessService;
    private final ProjectService projectService;
    private final UserService userService;

    public TasksService(
        TasksRepository tasksRepository,
        ProjectAccessService accessService,
        ProjectService projectService,
        UserService userService
    ) {
        this.tasksRepository = tasksRepository;
        this.accessService = accessService;
        this.projectService = projectService;
        this.userService = userService;
    }

    public ResponseEntity<?> getTasksForProject(Integer projectId, Principal principal) {
        Users user = userService.getByMail(principal.getName());
        Project project = projectService.retrieveProjectById(projectId);

        if (!accessService.hasRole(user, project, ProjectRole.ADMIN, ProjectRole.MANAGER, ProjectRole.DEVELOPER, ProjectRole.TESTER, ProjectRole.VIEWER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }

        List<Tasks> tasks = tasksRepository.findByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    public ResponseEntity<?> createTask(Tasks task, Principal principal) {
        Users user = userService.getByMail(principal.getName());
        Project project = task.getProject();

        if (project == null) {
            return ResponseEntity.badRequest().body("Project is required");
        }

        if (!accessService.hasRole(user, project, ProjectRole.ADMIN, ProjectRole.MANAGER, ProjectRole.DEVELOPER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient permissions");
        }

        tasksRepository.save(task);
        return ResponseEntity.ok("Task created");
    }

    public ResponseEntity<?> updateTask(int id, Tasks updatedTask, Principal principal) {
        Optional<Tasks> optExisting = tasksRepository.findById(id);
        if (optExisting.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tasks existing = optExisting.get();
        Users user = userService.getByMail(principal.getName());
        Project project = existing.getProject();

        if (!accessService.hasRole(user, project, ProjectRole.ADMIN, ProjectRole.MANAGER, ProjectRole.DEVELOPER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient permissions");
        }

        // Prevent project reassignment
        updatedTask.setProject(existing.getProject());

        updateTaskFields(existing, updatedTask);
        tasksRepository.save(existing);

        return ResponseEntity.ok("Task updated");
    }

    public ResponseEntity<?> deleteTask(int id, Principal principal) {
        Optional<Tasks> optTask = tasksRepository.findById(id);
        if (optTask.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tasks task = optTask.get();
        Users user = userService.getByMail(principal.getName());
        Project project = task.getProject();

        if (!accessService.hasRole(user, project, ProjectRole.ADMIN, ProjectRole.MANAGER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient permissions");
        }

        tasksRepository.deleteById(id);
        return ResponseEntity.ok("Task deleted");
    }

    private void updateTaskFields(Tasks existing, Tasks updated) {
        existing.setTitle(updated.getTitle());
        existing.setType(updated.getType());
        existing.setStatus(updated.getStatus());
        existing.setDescription(updated.getDescription());
        existing.setPriority(updated.getPriority());
        existing.setAssignee(updated.getAssignee());
        existing.setReporter(updated.getReporter());
    }


    public List<Tasks> retrieveTasksByProjectId(Integer projectId) {
        return tasksRepository.findByProjectId(projectId);
    }
}
