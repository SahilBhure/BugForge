package com.bugforge.BugForge.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity(name = "USER_PROJECTS")
public class UserProjects {

    @Id
    @GeneratedValue
    int id;

    @ManyToOne
    @JoinColumn(name = "users_id", referencedColumnName = "id")
    Users user;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    Project project;

    public UserProjects() {}

    public UserProjects(int id, Users user, Project project) {
        super();
        this.id = id;
        this.user = user;
        this.project = project;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    @Override
    public String toString() {
        return "UserProjects [id=" + id + ", user=" + user + ", project=" + project + "]";
    }
}
