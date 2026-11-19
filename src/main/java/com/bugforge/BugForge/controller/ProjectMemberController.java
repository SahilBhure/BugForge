package com.bugforge.BugForge.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugforge.BugForge.data.Project;
import com.bugforge.BugForge.data.ProjectMembership;
import com.bugforge.BugForge.data.ProjectMembershipRepository;
import com.bugforge.BugForge.data.ProjectRole;
import com.bugforge.BugForge.data.Users;
import com.bugforge.BugForge.service.ProjectAccessService;
import com.bugforge.BugForge.service.ProjectService;
import com.bugforge.BugForge.service.UserService;

@RestController
@RequestMapping("/projects/{projectId}/members")
public class ProjectMemberController {

    private final UserService userService;
    private final ProjectService projectService;
    private final ProjectAccessService accessService;
    private final ProjectMembershipRepository membershipRepo;

    public ProjectMemberController(
        UserService userService,
        ProjectService projectService,
        ProjectAccessService accessService,
        ProjectMembershipRepository membershipRepo
    ) {
        this.userService = userService;
        this.projectService = projectService;
        this.accessService = accessService;
        this.membershipRepo = membershipRepo;
    }

    @GetMapping
    public ResponseEntity<?> getMembers(@PathVariable Integer projectId, Principal principal) {
        Users currentUser = userService.getByMail(principal.getName());
        Project project = projectService.retrieveProjectById(projectId);

        if (!accessService.hasRole(currentUser, project,
                ProjectRole.ADMIN, ProjectRole.MANAGER, ProjectRole.DEVELOPER, ProjectRole.TESTER, ProjectRole.VIEWER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        List<ProjectMembership> members = membershipRepo.findByProject(project);
        return ResponseEntity.ok(members);
    }

    @PostMapping
    public ResponseEntity<?> addMember(
        @PathVariable Integer projectId,
        @RequestBody Map<String, String> payload,
        Principal principal
    ) {
        Users currentUser = userService.getByMail(principal.getName());
        Project project = projectService.retrieveProjectById(projectId);

        if (!accessService.hasRole(currentUser, project, ProjectRole.ADMIN, ProjectRole.MANAGER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only ADMIN or MANAGER can add members");
        }

        String email = payload.get("email");
        String roleStr = payload.get("role");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        ProjectRole role = ProjectRole.DEVELOPER;
        if (roleStr != null && !roleStr.isBlank()) {
            try {
                role = ProjectRole.valueOf(roleStr.toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid role: " + roleStr);
            }
        }

        Users targetUser;
        try {
            targetUser = userService.getByMail(email.trim());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with email: " + email);
        }

        Optional<ProjectMembership> existing = membershipRepo.findByUserAndProject(targetUser, project);
        if (existing.isPresent()) {
            ProjectMembership m = existing.get();
            m.setRole(role);
            membershipRepo.save(m);
            return ResponseEntity.ok(m);
        }

        ProjectMembership newMembership = new ProjectMembership(targetUser, project, role);
        ProjectMembership saved = membershipRepo.save(newMembership);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{membershipId}")
    public ResponseEntity<?> updateMemberRole(
        @PathVariable Integer projectId,
        @PathVariable Long membershipId,
        @RequestBody Map<String, String> payload,
        Principal principal
    ) {
        Users currentUser = userService.getByMail(principal.getName());
        Project project = projectService.retrieveProjectById(projectId);

        if (!accessService.hasRole(currentUser, project, ProjectRole.ADMIN, ProjectRole.MANAGER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only ADMIN or MANAGER can update member roles");
        }

        Optional<ProjectMembership> opt = membershipRepo.findById(membershipId);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProjectMembership m = opt.get();
        String roleStr = payload.get("role");
        if (roleStr == null || roleStr.isBlank()) {
            return ResponseEntity.badRequest().body("Role is required");
        }

        try {
            ProjectRole role = ProjectRole.valueOf(roleStr.toUpperCase().trim());
            m.setRole(role);
            membershipRepo.save(m);
            return ResponseEntity.ok(m);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role: " + roleStr);
        }
    }

    @DeleteMapping("/{membershipId}")
    public ResponseEntity<?> removeMember(
        @PathVariable Integer projectId,
        @PathVariable Long membershipId,
        Principal principal
    ) {
        Users currentUser = userService.getByMail(principal.getName());
        Project project = projectService.retrieveProjectById(projectId);

        if (!accessService.hasRole(currentUser, project, ProjectRole.ADMIN, ProjectRole.MANAGER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only ADMIN or MANAGER can remove members");
        }

        Optional<ProjectMembership> opt = membershipRepo.findById(membershipId);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProjectMembership m = opt.get();
        if (project.getOwner() != null && m.getUser().getId() == project.getOwner().getId()) {
            return ResponseEntity.badRequest().body("Cannot remove the project owner");
        }

        membershipRepo.delete(m);
        return ResponseEntity.ok("Member removed");
    }
}
