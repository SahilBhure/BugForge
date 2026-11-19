package com.bugforge.BugForge.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer>{
    List<Project> findAllByOwner_mail(String email);

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN p.memberships m WHERE p.owner.mail = :email OR m.user.mail = :email")
    List<Project> findAllUserProjects(@Param("email") String email);
}
