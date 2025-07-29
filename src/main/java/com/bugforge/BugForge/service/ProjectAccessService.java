package com.bugforge.BugForge.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bugforge.BugForge.data.*;

@Service
public class ProjectAccessService {
	
	@Autowired
    private ProjectMembershipRepository membershipRepo;

	public boolean hasRole(Users user, Project project, ProjectRole... allowedRoles) {
	    return membershipRepo.findByUserAndProject(user, project)
	        .filter(membership -> Arrays.asList(allowedRoles).contains(membership.getRole()))
	        .isPresent();
	}


    public ProjectRole getUserRole(Users user, Project project) {
        return membershipRepo.findByUserAndProject(user, project)
            .map(ProjectMembership::getRole)
            .orElse(null);
    }

}
