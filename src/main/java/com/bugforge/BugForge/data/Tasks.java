package com.bugforge.BugForge.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class Tasks {

    @Id
    @GeneratedValue
    int id;
    String title;
    Type type;
    Status status;
    String description;
    Priority priority;

    @ManyToOne
    @JoinColumn(name = "assignee_id", referencedColumnName = "id")
    Users assignee;

    @ManyToOne
    @JoinColumn(name = "reporter_id", referencedColumnName = "id")
    Users reporter;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    Project project;

    public enum Type {
        BUG, FEATURE
    }

    public enum Status {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public Tasks() {}

    public Tasks(int id, String title, Type type, Status status, String description, Priority priority, Users assignee,
                Users reporter, Project project) {
        super();
        this.id = id;
        this.title = title;
        this.type = type;
        this.status = status;
        this.description = description;
        this.priority = priority;
        this.assignee = assignee;
        this.reporter = reporter;
        this.project = project;
    }

    // Getters and setters for all fields

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Users getAssignee() { return assignee; }
    public void setAssignee(Users assignee) { this.assignee = assignee; }

    public Users getReporter() { return reporter; }
    public void setReporter(Users reporter) { this.reporter = reporter; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    @Override
    public String toString() {
        return "Tasks [id=" + id + ", title=" + title + ", type=" + type + ", status=" + status + ", description="
                + description + ", priority=" + priority + ", assignee=" + assignee + ", reporter=" + reporter
                + ", project=" + project + "]";
    }
}
