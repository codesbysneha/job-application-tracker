# Job Application Tracker

A RESTful backend application built with Java and Spring Boot to help users manage and track their job applications in one place.

## About the Project

The Job Application Tracker allows users to maintain a record of their job applications, monitor application statuses, search and filter applications, and view dashboard statistics.

This project was developed to practise backend development using Spring Boot, REST APIs, PostgreSQL, and automated testing.

## Features

- **Create Applications:** Add new job applications with company name, job title, location, job type, salary, applied date, job URL, and notes.
- **View Applications:** Retrieve all applications or a specific application by ID.
- **Update Applications:** Modify existing application details.
- **Delete Applications:** Remove applications with appropriate HTTP responses.
- **Search Applications:** Search by company name, including partial and case-insensitive matches.
- **Filter Applications:** Filter applications by status.
- **Dashboard Statistics:** View total applications and counts for each application status.
- **Pagination and Sorting:** Retrieve application records in manageable pages.
- **Input Validation:** Validate required fields, dates, URLs, and field lengths.
- **Exception Handling:** Return appropriate error responses for invalid requests and missing records.
- **CORS Configuration:** Allow requests from configured frontend development origins.
- **API Documentation:** Explore and test endpoints using Swagger UI.
- **Automated Testing:** Includes service, controller, validation, and application context tests.

## Technologies Used

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.1.2 | Backend framework |
| Spring Data JPA | Database operations |
| Hibernate | ORM |
| PostgreSQL | Relational database |
| Maven | Build and dependency management |
| Jakarta Validation | Input validation |
| JUnit and Mockito | Automated testing |
| Swagger / OpenAPI | API documentation |
| IntelliJ IDEA | Development environment |

## Project Structure

```text
src
 ├── main
 │    ├── java/com/sneha/jobapplicationtracker
 │    │    ├── controller
 │    │    ├── service
 │    │    ├── repository
 │    │    ├── entity
 │    │    ├── dto
 │    │    ├── enums
 │    │    ├── exception
 │    │    └── JobApplicationTrackerApplication.java
 │    │
 │    └── resources
 │         └── application.properties
 │
 └── test
      └── java/com/sneha/jobapplicationtracker
```

## API Endpoints

Base URL:

```text
http://localhost:8080/api/applications
```

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/applications` | Create a job application |
| GET | `/api/applications` | Retrieve all applications |
| GET | `/api/applications/{id}` | Retrieve an application by ID |
| PUT | `/api/applications/{id}` | Update an application |
| DELETE | `/api/applications/{id}` | Delete an application |
| GET | `/api/applications/search?companyName=Google` | Search by company name |
| GET | `/api/applications/filter?status=Applied` | Filter by status |
| GET | `/api/applications/dashboard/stats` | Retrieve dashboard statistics |
| GET | `/api/applications/page?page=0&size=5` | Retrieve paginated applications |

## Application Statuses

The application supports the following statuses:

- Applied
- Interview
- Offered
- Rejected
- Withdrawn

## Sample API Request

### Create a Job Application

**POST** `/api/applications`

Request body:

```json
{
  "companyName": "Google",
  "jobTitle": "Software Engineer",
  "location": "Bangalore",
  "jobType": "Full-time",
  "status": "Applied",
  "appliedDate": "2026-11-14",
  "jobUrl": "https://careers.google.com/jobs/results/123456",
  "salary": 2500000,
  "notes": "Applied through LinkedIn"
}
```

Successful response: `201 Created`

## Swagger API Documentation

This project uses Swagger UI to provide interactive API documentation.

After starting the application, visit:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON documentation:

```text
http://localhost:8080/v3/api-docs
```

Swagger allows users to explore endpoints, view request and response formats, and execute API requests directly from the browser.

## Getting Started

### Prerequisites

Make sure you have installed:

- JDK 21
- PostgreSQL
- IntelliJ IDEA (or another Java IDE)
- Git

### 1. Clone the Repository

```bash
git clone <your-github-repository-url>
```

### 2. Open the Project

Open the cloned project folder in IntelliJ IDEA.

### 3. Create the PostgreSQL Database

Open PostgreSQL and execute:

```sql
CREATE DATABASE job_application_tracker;
```

### 4. Configure Database Credentials

The project uses an environment variable for the PostgreSQL password.

In the IntelliJ PowerShell terminal, set:

```powershell
$env:DB_PASSWORD="your-postgresql-password"
```

The database configuration in `application.properties` uses this environment variable:

```properties
spring.application.name=job-application-tracker

spring.datasource.url=jdbc:postgresql://localhost:5432/job_application_tracker
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

**Important:** Never commit your actual database password to GitHub.

### 5. Run the Application

In the project terminal, execute:

```powershell
.\mvnw.cmd spring-boot:run
```

The backend application will start at:

```text
http://localhost:8080
```

### 6. Run Automated Tests

Execute:

```powershell
.\mvnw.cmd test
```

The project currently has **33 automated tests**, with all 33 passing.

## CORS Configuration

The backend is configured to allow frontend development requests from:

- `http://localhost:3000`
- `http://localhost:5173`

## Future Improvements

- Build a frontend using React.
- Add user authentication and authorization.
- Introduce user-specific job application records.
- Deploy the application to a cloud platform.
- Add additional dashboard visualizations.

## Author

Developed as a personal backend development project to strengthen Java, Spring Boot, REST API, and database management skills.