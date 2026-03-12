# 📚 Task Management Application - Spring Boot Back-End

![Java](https://img.shields.io/badge/Java-19-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-32CD32)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6.2.3-FF69B4)
![JWT](https://img.shields.io/badge/JJWT-0.11.5-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.3-FFD700)
![Hibernate](https://img.shields.io/badge/Hibernate-6.4.4.Final-4B0082)
![Liquibase](https://img.shields.io/badge/Liquibase-4.24-blue)
![Architecture](https://img.shields.io/badge/Architecture-Layered-lightgreen)
![DTO](https://img.shields.io/badge/DTO-Mapping-yellow)
![Specification](https://img.shields.io/badge/Specification-Pattern-lightgrey)
![Exception](https://img.shields.io/badge/Exception-Handling-red)
![Repository](https://img.shields.io/badge/Repository-Pattern-purple)
![Security](https://img.shields.io/badge/Security-Pattern-teal)
![Validation](https://img.shields.io/badge/Validation-Spring%20Validation-green)
![Testing](https://img.shields.io/badge/Testing-JUnit%205%20%7C%20Mockito%205.7%20%7C%20Integration-blueviolet)
![Docker](https://img.shields.io/badge/Docker-27.1.1-2496ED)
![Docker Compose](https://img.shields.io/badge/Docker_Compose-2.29.1-skyblue)
![Dropbox SDK](https://img.shields.io/badge/Dropbox%20SDK-5.4.4-0061FF)
![Google Calendar API](https://img.shields.io/badge/Google%20Calendar-API-4285F4)
![OAuth2](https://img.shields.io/badge/Authentication-OAuth2-FB8C00)
![Email](https://img.shields.io/badge/Email-SMTP%20%28Gmail%29-EA4335)
![Telegram](https://img.shields.io/badge/Telegram-Bot%20API-26A5E4)
![Swagger](https://img.shields.io/badge/Swagger%20UI-5.13-85EA2D)

## 📌 Introduction

**Task Management Application** is a production-style back-end system designed to
manage personal and team tasks while integrating with external productivity and
communication services.

The application demonstrates how modern enterprise systems orchestrate business
workflows and external integrations as:

- task lifecycle management
- secure authentication and authorization
- event-driven notifications
- calendar synchronization
- file storage integration
- encrypted credential management

The primary goal of this project is to showcase clean architecture, integration design, and
scalable system orchestration rather than simple CRUD functionality.

The system is built following production-grade engineering principles:

- layered architecture
- event-driven communication
- DTO mapping
- asynchronous processing
- encrypted external credentials storage
- custom exception handling
- SpecificationProvider
- SpecificationBuilder
- service-level security
- JWT-based authentication
- service-level validation
- liquibase
- mocking and integration testing
- MySQL database
- Docker containerization

---

## 🎯 Motivation

This project was created to simulate a real productivity platform capable of integrating
with multiple external services while maintaining a secure and maintainable architecture.

Key engineering challenges addressed in this project:

- 🧩 Organization controllers, services, and repositories into clean architecture
- 📡 Implementing event-driven task notifications across multiple channels
- 🔗 Designing a task lifecycle integrated with external notification systems
- 🔐 Secure storage and encryption of third-party credentials
- ☁ Integrating with cloud APIs(Dropbox, Google Calendar, Email, Telegram)
- ⚙ Managing asynchronous execution of external service calls
- 🧱 Designing scalable integration layers and configuration abstractions
- 🧪 Writing mocking and integration tests that simulate real workflow
  (register -> login -> create project -> create task)

---

## 📌 Features / Functionality

👤 **User registration, authentication and security**

- Registration and login
- JWT tokens issuance
- Role-based access control (USER / ADMIN)
- Secure storage of external service credentials
- Encryption of sensitive user data

---
📝 **Project management**

- Create, read, update, and delete projects
- Project name and description
- Start/end date
- Project lifecycle management

---
📝 **Task management**

- Create, read, update, and delete tasks
- Task scheduling
- Task ownership and assignment
- Filtering and searching tasks
- Task lifecycle management

---
📡 **Notification System**

The application automatically reacts to task lifecycle events and events
and triggers external notifications:

- Telegram notifications
- Email notifications
- Google Calendar event synchronization

---

## ☁ External Integrations

**Google Calendar Integration**

- OAuth2 authorization
- Automatic event creation
- Task scheduling synchronization

**Email Integration**

- SMTP notifications
- Secure encrypted credential storage

**Telegram Integration**

- Bot-based notification delivery
- User chat binding

**Dropbox Integration**

- File upload for task attachments
- External file storage management

---

- **⚡ Asynchronous Processing**
- External API executed asynchronously
- Improves application performance and responsiveness
- Prevent blocking business operations

---

## 🏗 Architecture & Technology Stack

The application follows a layered + integration-oriented architecture,
designed for scalability and maintainability.

---
🔹 Presentation Layer(Controllers)

Responsible for:

- REST endpoint exposure
- Request validation
- DTO mapping
- Security entry points

---
🔹 Repository Layer

Responsible for:

- Database communication
- Entity persistence via Spring Data JPA

---
🔹 DTO Layer

Responsible for:

- API contract stability
- Data transformation between layers
- Validation boundaries

---
🔹 Security Layer

Includes:

- JWT authentication
- Role-based authorization
- Credential encryption services

---
🔹 Integration Layer (External Services)

Handles communication with third-party services through specialized services
and configuration modules:

- GoogleOAuthService
- GoogleCalendarService
- TelegramNotificationService
- EmailNotificationService
- Dropbox file storage integration

Integration infrastructure includes:

- Credentials providers
- Factory pattern for API clients
- Configuration abstraction
- Property-based environment configuration

---
🔹 Event & Listener Layer

Implement event-driven workflow using:

- Task lifecycle listeners
- Notification orchestration

Examples:

- GoogleCalendarTaskListener
- EmailNotificationTaskListener
- TelegramNotificationTaskListener

---
🔹 Configuration Layer
Centralized infrastructure configuration:

- SecurityEmailEncryptionConfiguration
- GoogleCalendarConfiguration
- DropboxConfiguration
- RestTemplateConfiguration
- AsyncConfiguration
- SwaggerConfiguration

---
🔹 Async Processing Layer

- Executes external service operations asynchronously
- Improves scalability
- Prevent bocking HTTP requests

---
🔹 Testing Layer

Includes:

- Unit tests (Mockito)
- Integration tests

---

## Technology Stack

| Technology / Tool               | Version        | Purpose                                                                                                      |
|---------------------------------|----------------|--------------------------------------------------------------------------------------------------------------|
| Java                            | 19             | Core programming language                                                                                    |
| Spring Boot                     | 3.2.4          | Application framework                                                                                        |
| Spring Security + JWT           | 6.2.3 + 0.11.5 | Authentication & authorization                                                                               |
| MySQL                           | 8.3            | Database                                                                                                     |
| Hibernate                       | 6.4.4.Final    | ORM framework; handles database persistence, mapping Java entities to database tables , and query generation |
| Liquibase                       | 4.24           | Database migration & versioning                                                                              |                                                                                                                                                                         |
| Mocking & Integration Testing   | 5.7            | Mock and integration testing of application layers                                                           |
| Docker                          | 27.1.1         | Containerization of application & MySQL DB                                                                   |
| Docker Compose                  | 2.29.1         | Orchestrates containers                                                                                      |
| Swagger                         | 5.13           | API documentation & testing                                                                                  |

## External Integrations & SDK`s

| Integration / SDK                 | Version                    | Purpose                                                                 |
|----------------------------------|----------------------------|-------------------------------------------------------------------------|
| Google Calendar API               | v3                         | Task scheduling & calendar synchronization                              |
| Google API Client                | 2.2.0                      | Core Google API client infrastructure                                   |
| Google OAuth2 Client             | 1.23.0                     | OAuth2 authentication & authorization                                   |
| Dropbox Core SDK                 | 5.4.4                      | File upload & external file storage                                     |
| Telegram Bot API                 | Latest                     | Task notifications via Telegram bot                                     |
| Email (SMTP – Gmail)             | SMTP                       | Email notifications                                                      |

## Design Patterns && Architecture Concepts

| Concept / Pattern               | Purpose                                               |
|---------------------------------|-------------------------------------------------------|
| Layered Architecture            | Maintainable, scalable system, separation of concerns |
| Repository Pattern              | Clean separation of data access logic, abstraction    |
| SpecificationProvider & Builder | Dynamic filtering/search in catalog                   |
| Validation (Spring Validation)  | DTO input validation                                  |
| Factory Pattern                 | External API client creation                          |
| Listener / Event Pattern        | Notification orchestration                            |
| DTO Pattern                     | Stable API contract                                   |
| Encryption Service Pattern      | Secure credential storage                             |
| Async Processing Pattern        | External API performance                              |

## UML Diagram

![UML Diagram](Domain%20model.png)    
![UML Diagram](Application%20architectre.png)
![UML Diagram](System%20integration.png)

---

## 🐳 Infrastructure & Deployment

The application is fully containerized using Docker and Docker Compose.

Docker Compose orchestrates:

- Spring Boot Application
- MySQL Database
- Liquibase migrations
- Environment configuration

This approach guarantees:

- reproducible environment
- simplified deployment
- consistent behavior across systems

---

## 🛠 Local Setup / Getting Started

### 1️⃣ Prerequisites

Make sure you have the following software installed:

 ```bash 
Docker
Docker Compose
Git
A web browser (to access Swagger UI)
```

---

### 2️⃣ Run using Docker Compose(Recommended)

 ```bash  
  git clone https://github.com/VitaliyProgrammer/task-management-application.git
  cd task-management-application
  docker compose up --build
  ```

Application will be available at:

```bash 
http://localhost:8080
```

🛑 Stop the Application

```bash
docker compose down
```

📦 Run using Pre-built Docker Image
The application image is available on Docker Hub:

```bash
docker pull vitaliyjavaprog/task-management-application:1.0.1
```

⚠️This image requires MySQL database.
For full environment setup, using Docker Compose is recommended.️

---

## External Services Used

- Google Calendar API
- Telegram Bot API
- Dropbox API
- SMTP Email Services

---

## 📘 API Documentation

All endpoints are documented in Swagger UI:
[Open Swagger UI](http://localhost:9090/swagger-ui/index.html)

---

## 🎥 Video Presentation

---
A short video walk through of the Task Management application is available here:

The video demonstrates:

- User registration and authentication workflow
- Task lifecycle management
- Swagger API demonstration
- External integrations workflow:
    - Telegram notifications
    - Email notifications
    - Google Calendar synchronization
    - Dropbox file upload

👉 https://drive.google.com/file/d/1ePOL9sHcYB-oYl8B11FDmNZyemhO3a7x/view?usp=sharing

---

## 📌 Final Notes

This project reflects my approach to back-end engineering:

- clean and scalable architecture design
- integrations-driven system orchestration
- security-first handling of external credentials
- asynchronous processing of external services
- realistic workflow modeling based on production practices

The application is designed not only a learning milestone but also as a demonstration
of how modern back-end systems integrate with third-party platforms while maintaining
reliability, maintainability, and security.