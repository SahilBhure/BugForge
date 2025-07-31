# 🐞 BugForge

BugForge is a lightweight issue and project management system inspired by Jira. It allows teams to manage users, projects, and tasks efficiently using a role-based access control system.

---

## 📌 Features

- 🔐 User Authentication (Spring Security + JWT)
- 🧑‍🤝‍🧑 Role-Based Access Control (Global & Project-specific)
- 🗂️ Project Management (Create, update, assign members)
- 📋 Task Management (Independent of project, but assignable)
- 🧾 RESTful APIs for all entities
- 📦 Modular architecture with DTOs and service layers
- 📊 Admin/User/Project Owner permissions

---

## 🛠️ Technologies Used

- **Backend**: Java 17, Spring Boot
- **Security**: Spring Security, JWT
- **Database**: PostgreSQL / MySQL / H2 (configurable)
- **Build Tool**: Maven
- **Documentation**: Swagger (optional)

---

## 📁 Project Structure

```
com.bugforge.BugForge
│
├── controller        # REST Controllers
├── dto               # Data Transfer Objects
├── entity            # JPA Entities (User, Project, Task, etc.)
├── exception         # Custom exception classes
├── repository        # Spring Data JPA Repositories
├── security          # JWT & Spring Security Config
├── service           # Business logic layer
└── util              # Utility classes (if any)
```

---

## 🚀 Getting Started

### ✅ Prerequisites

- Java 17+
- Maven
- MySQL or any relational DB
- IDE (IntelliJ / VS Code)
- Postman or similar API testing tool

---

## ⚙️ Setup Instructions

### 1. Clone the Repo

```bash
git clone https://github.com/your-username/BugForge.git
cd BugForge
```

### 2. Configure the Database

Update the `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bugforge
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT config
bugforge.jwt.secret=a-string-secret-at-least-256-bits-long
bugforge.jwt.expirationMs=86400000
```

> You can switch to `H2` for testing if needed.

---

### 3. Build & Run

```bash
# Build the project
mvn clean install

# Run the project
mvn spring-boot:run
```

App will start on:  
`http://localhost:8080`

---

## 🔑 Authentication

### Sign up

`POST /api/auth/register`  
Request Body:
```json
{
  "name": "your name",
  "email": "example@mail.com",
  "password": "yourPassword"
}
```

### Login

`POST /api/auth/login`  
Returns a JWT token:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Use this token in all protected endpoints via:  
`Authorization: Bearer <token>`

---

## 📘 API Overview

### User APIs
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/user` — Get current user

### Project APIs
- `POST /api/projects` — Create project
- `GET /api/projects` — Get all


### Task APIs
- `POST /api/tasks` — Create task
- `GET /api/tasks` — List tasks
- `GET /api/projects/{id}/tasks` — Tasks in project

---

## 🧪 Testing API

You can test APIs using Postman or any REST client.

Make sure to:

- Add the JWT token in **Authorization header**
- Set **Content-Type** to `application/json`

---

## 🤝 Contributing

Contributions are welcome! To contribute:

1. Fork the repo
2. Create a new branch (`feature/xyz`)
3. Commit your changes
4. Push and create a PR

---

## 📝 License

MIT License (or update based on your preference)