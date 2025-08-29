package com.bugforge.BugForge.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

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

//    @ManyToMany
//    @JoinTable(
//        name = "project_members",
//        joinColumns = @JoinColumn(name = "project_id"),
//        inverseJoinColumns = @JoinColumn(name = "user_id")
//    )
//    List<Users> members;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectMembership> memberships = new ArrayList<>();

    
    public List<ProjectMembership> getMemberships() {
		return memberships;
	}

	public void setMemberships(List<ProjectMembership> memberships) {
		this.memberships = memberships;
	}

	public Project() {}

    public Project(int id, String name, String description, Users owner, LocalDate startDate, LocalDate endDate) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.startDate = startDate;
        this.endDate = endDate;
        //this.members = members;
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

//	public List<Users> getMembers() { return members; }
//	public void setMembers(List<Users> members) { this.members = members; }
    
   
}
