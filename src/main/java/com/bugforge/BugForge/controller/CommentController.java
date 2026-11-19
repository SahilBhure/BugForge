package com.bugforge.BugForge.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugforge.BugForge.data.Comment;
import com.bugforge.BugForge.data.CommentRepository;
import com.bugforge.BugForge.data.Project;
import com.bugforge.BugForge.data.ProjectRole;
import com.bugforge.BugForge.data.Tasks;
import com.bugforge.BugForge.data.TasksRepository;
import com.bugforge.BugForge.data.Users;
import com.bugforge.BugForge.service.ProjectAccessService;
import com.bugforge.BugForge.service.UserService;

@RestController
@RequestMapping("/tasks/{taskId}/comments")
public class CommentController {

    private final CommentRepository commentRepository;
    private final TasksRepository tasksRepository;
    private final UserService userService;
    private final ProjectAccessService accessService;

    public CommentController(
        CommentRepository commentRepository,
        TasksRepository tasksRepository,
        UserService userService,
        ProjectAccessService accessService
    ) {
        this.commentRepository = commentRepository;
        this.tasksRepository = tasksRepository;
        this.userService = userService;
        this.accessService = accessService;
    }

    @GetMapping
    public ResponseEntity<?> getComments(@PathVariable int taskId, Principal principal) {
        Optional<Tasks> optTask = tasksRepository.findById(taskId);
        if (optTask.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tasks task = optTask.get();
        Users currentUser = userService.getByMail(principal.getName());
        Project project = task.getProject();

        if (project != null && !accessService.hasRole(currentUser, project,
                ProjectRole.ADMIN, ProjectRole.MANAGER, ProjectRole.DEVELOPER, ProjectRole.TESTER, ProjectRole.VIEWER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        List<Comment> comments = commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<?> addComment(
        @PathVariable int taskId,
        @RequestBody Map<String, String> payload,
        Principal principal
    ) {
        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Comment content cannot be empty");
        }

        Optional<Tasks> optTask = tasksRepository.findById(taskId);
        if (optTask.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tasks task = optTask.get();
        Users currentUser = userService.getByMail(principal.getName());
        Project project = task.getProject();

        if (project != null && !accessService.hasRole(currentUser, project,
                ProjectRole.ADMIN, ProjectRole.MANAGER, ProjectRole.DEVELOPER, ProjectRole.TESTER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient permissions to comment");
        }

        Comment comment = new Comment(content.trim(), currentUser, task);
        Comment saved = commentRepository.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
        @PathVariable int taskId,
        @PathVariable Long commentId,
        Principal principal
    ) {
        Optional<Comment> optComment = commentRepository.findById(commentId);
        if (optComment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Comment comment = optComment.get();
        Users currentUser = userService.getByMail(principal.getName());
        Tasks task = comment.getTask();
        Project project = task != null ? task.getProject() : null;

        boolean isAuthor = comment.getAuthor() != null && comment.getAuthor().getId() == currentUser.getId();
        boolean isManagerOrAdmin = project != null && accessService.hasRole(currentUser, project, ProjectRole.ADMIN, ProjectRole.MANAGER);

        if (!isAuthor && !isManagerOrAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete your own comments");
        }

        commentRepository.delete(comment);
        return ResponseEntity.ok("Comment deleted");
    }
}
