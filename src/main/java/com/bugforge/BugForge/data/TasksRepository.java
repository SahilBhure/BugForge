package com.bugforge.BugForge.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Integer>{

	List<Tasks> findByProjectId(Integer projectId);

}
