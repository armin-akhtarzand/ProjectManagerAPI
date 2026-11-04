# ProjectManagerAPI

A REST API for managing projects, project members, tasks, and comments.

## Purpose

I built this project as a test to see if I could build something myself without my teacher's guidance.
It is mainly a learning project for myself, but also something I can come back to in the future when I want to build a similar application that I will use.

I wanted to learn how to become a more independent developer and put the things I have learned in school together in one project.

I used ChatGPT as a consultant to discuss my ideas, possible security risks, and which relationships would make sense.
I wrote the application code myself. AI helped write the Task and Comment service and controller tests.

This is still a work in progress. Some security improvements and tests are planned before I consider it finished.

## Features

- **User management:** create, read, update, and delete users, with permissions for different actions. Users have a global position (`ADMIN` or `EMPLOYEE`) and a status (`ACTIVE`, `INACTIVE`, or `VACATION`).
- **Project management:** create projects, update their information, change their status, and delete them.
- **Project memberships:** add and remove members, and give users a role within a specific project.
- **Tasks:** create tasks, assign them to project members, set deadlines, and update their content and status.
- **Comments:** create, read, update, and delete comments connected to tasks. The author comes from the logged-in user, not from the request body.
- **Authentication:** log in with an email and password and use a JWT for protected requests.
- **Validation and error handling:** validate request data and return HTTP error responses through a shared exception handler.

## Tech Stack

- Java 17 (target version)
- Spring Boot 4.0.3
- Spring Security and JWT
- Spring Data JPA / Hibernate
- PostgreSQL for the application
- H2 for the test profile
- Maven, with Maven Wrapper included
- JUnit, Mockito, and MockMvc
- Lombok
- Swagger / OpenAPI through springdoc 3.0.2

## Getting Started

### Prerequisites

- JDK 17 or newer.
- A PostgreSQL database for normal application use.
- An RSA key pair for signing and verifying JWTs.

The project includes Maven Wrapper, so a separate Maven installation is not required.

### Configuration

Set these environment variables in your terminal or in your IDE's run configuration:

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_URL` | PostgreSQL JDBC URL, for example `jdbc:postgresql://localhost:5432/projectmanager` | Required |
| `DB_USERNAME` | Database username | Required |
| `DB_PASSWORD` | Database password | Required |
| `JWT_PUBLIC_KEY` | Base64-encoded RSA public key in X.509 DER format | Required |
| `JWT_PRIVATE_KEY` | Base64-encoded RSA private key in PKCS#8 DER format | Required |
| `SERVER_PORT` | Port used by the application | `8080` |
| `JWT_ISSUER` | The issuer used when creating and verifying JWTs | `http://localhost:8080` |
| `JWT_EXPIRATION_MINUTES` | How long a token is valid | `60` |
| `JWT_KEY_ID` | ID used to identify the signing key | `jwt-key-1` |

The key values should not include PEM headers such as `-----BEGIN PRIVATE KEY-----`.
Keep passwords and private keys outside the repository. Spring Boot does not automatically load a plain `.env` file in this project.

### Database Setup and First Admin

The normal configuration uses `spring.jpa.hibernate.ddl-auto=none`, so the application expects the database tables to exist.
There is currently no migration or seed script included.

For a new, empty local development database, Hibernate can create the tables by starting the application with:

```bash
SPRING_JPA_HIBERNATE_DDL_AUTO=update ./mvnw spring-boot:run
```

Only use this option against your own development database. Database migrations are planned as an improvement.

Creating users through the API requires an existing admin. There is currently no public registration endpoint or automatic first-admin setup.
Before using the API, add a development admin directly to your local database with a BCrypt-hashed password, position `ADMIN`, and status `ACTIVE`, including the required name and creation date fields.
Do not store the password as plain text.

This manual setup is a current limitation, and I want to make it easier to get started with the project.

### Running the Application

After configuring the environment and database, run:

```bash
./mvnw spring-boot:run
```

The default application URL is `http://localhost:8080`.

## Using the API

### API Documentation

When the application is running, open:

```text
http://localhost:8080/swagger-ui/index.html
```

The OpenAPI JSON is available at `/v3/api-docs`.

### Authentication

Send a request to `POST /auth/login` with the credentials of an existing user:

```json
{
  "email": "admin@example.com",
  "password": "your-development-password"
}
```

This is an example, not an included account.

The response contains `accessToken`, `expiresIn` in seconds, `subject`, and `roles`.
Use the token in protected requests:

```text
Authorization: Bearer <accessToken>
```

In Swagger, use the Authorize button to provide the token.
The API is stateless and does not use a server-side login session. Project roles are checked separately from the global roles in the JWT.

### Main Endpoints

These are the main routes. Swagger contains the request and response details.

| Area | Routes |
|------|--------|
| Authentication | `POST /auth/login`, `GET /auth/jwks` |
| Users | `/users`, `/users/{id}` and endpoints for password, position, and status |
| Projects | `/projects`, `/projects/{id}` and project status updates |
| Memberships | `/projects/members`, `/projects/{projectId}/members`, `/users/{userId}/projects` |
| Project roles | `PATCH /projects/members/role` |
| Tasks | `/projects/{projectId}/tasks` and `/projects/{projectId}/tasks/{taskId}` |
| Task changes | Task endpoints ending in `/status` or `/assignee` |
| Comments | `/projects/{projectId}/tasks/{taskId}/comments` and the same path ending in `/{commentId}` |

### Example Workflow

1. Log in as an admin.
2. Create the users and a project.
3. Add users to the project and assign a project manager if needed.
4. Create a task and assign it to a member of that project.
5. Add a comment while logged in as a project member.
6. Update the task status as the work progresses.

## Roles and Permissions

I separated global positions from project roles because a user can have different responsibilities in different projects.

- **ADMIN:** can create and delete users and projects, and manage project members and tasks.
- **EMPLOYEE:** does not automatically have permission to manage projects. The project membership and project role matter as well.
- **PROJECT_MANAGER:** can manage information, members, and tasks within the project where they have this role.
- **PROJECT_MEMBER:** belongs to a project without the manager role.

Some actions also depend on ownership. For example, a user can update their own profile, an assignee can change their task's status, and only the author can update their comment.
An admin or the relevant project manager can also delete a comment. Creating a comment currently requires project membership, including for admins.

User status and permissions are separate. Users can change their own status to `ACTIVE` or `VACATION`, but only an admin can set a user to `INACTIVE`.
Currently, `INACTIVE` is not used to disable login automatically.

The access rules are not finished yet. In particular, some read endpoints currently require login but do not check project membership. See the limitations below.

## Architecture and Design Decisions

### Controllers, Services, and Repositories

I separated the code so that controllers handle HTTP requests and responses, services handle the application rules, and repositories handle database access.
This makes it easier for me to find where a change belongs and to test the different parts separately.

### DTOs and Mappers

I use separate request and response DTOs to control what the client can send and what the API returns.
For example, the create-user request does not let the client choose their own ID or admin position, and the response does not contain the password hash.

Mappers handle the conversion between DTOs and entities. Some mappers still call services to fetch related entities.
I plan to move these lookups into the services so that the mappers have a clearer responsibility.

### ProjectUser

I use `ProjectUser` to connect a user to a project and store the role and join date for that membership.
A user can be a manager in one project and a regular member in another without receiving manager permissions everywhere.

### AuthorizationService

`SecurityConfig` controls access to endpoints based on authentication and global roles.
`AuthorizationService` checks whether the current user can perform an action on a specific resource, such as updating their own profile or managing a particular project.

## Testing

Run the test suite with:

```bash
./mvnw test
```

The tests cover:

- Service logic with JUnit and Mockito, including successful requests and rejected operations.
- Controller routes, request validation, status codes, and response data with MockMvc.
- Selected user-endpoint security rules with the real Spring Security configuration.
- Application startup with the test profile and an in-memory H2 database.

The Mockito tests do not prove that the database mappings and queries work against PostgreSQL.
The controller tests with mocked services also do not prove that the real authorization logic protects every resource.
Dedicated database tests and `AuthorizationService` tests are still planned.

## Known Limitations and Planned Improvements

- Add project-membership checks to read endpoints for project information, tasks, members, and comments where appropriate.
- Review what access remains after someone leaves a project, especially when they are still assigned to a task or own a comment.
- Add a database constraint that makes each user-and-project membership unique, including during simultaneous requests.
- Add dedicated database and authorization tests.
- Add database migrations and an easier way to set up the first development admin.
- Move database lookups out of mappers and into services.
- Decide how inactive accounts should affect login and existing tokens.

This is a learning project, not a production-ready system. I want to finish these parts before expanding it further.

## Lessons Learned

Building this project has made me think more about why I write code a certain way, instead of only trying to make it work.
One example is the difference between being logged in and actually having permission to change a specific resource.

I have also learned that a nested URL needs checks to make sure the project, task, and comment belong together.
Using different IDs in tests helps catch mistakes where parameters have accidentally switched places.

Another thing I have learned is that passing tests do not automatically mean the application is secure.
The tests only cover the situations I have written them for, so I still need to review the rules and test the cases that should be rejected.

If I started again, I would define the access rules and database setup earlier and keep the responsibilities of services and mappers clearer from the beginning.
