package com.bugforge.BugForge.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer>{
    List<Project> findAllByOwner_mail(String email);
	
	

}
