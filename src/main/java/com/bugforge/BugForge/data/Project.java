package com.bugforge.BugForge.data;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class Project {

    @Id
    @GeneratedValue
    int id;
    String name;
    String description;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    Users owner;

    LocalDate startDate;
    LocalDate endDate;

    public Project() {}

    public Project(int id, String name, String description, Users owner, LocalDate startDate, LocalDate endDate) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Users getOwner() { return owner; }
    public void setOwner(Users owner) { this.owner = owner; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    @Override
    public String toString() {
        return "Project [id=" + id + ", name=" + name + ", description=" + description + ", owner=" + owner
                + ", startDate=" + startDate + ", endDate=" + endDate + "]";
    }
}
