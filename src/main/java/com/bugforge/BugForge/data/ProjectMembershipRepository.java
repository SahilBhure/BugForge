package com.bugforge.BugForge.data;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMembershipRepository extends JpaRepository<ProjectMembership, Long>{
	
	Optional<ProjectMembership> findByUserAndProject(Users user, Project project);
    List<ProjectMembership> findByUser(Users user);
    List<ProjectMembership> findByProject(Project project);
	
}
