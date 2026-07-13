# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.sava.teachernet.service.TeacherServiceTest"

# Run a single test method
./gradlew test --tests "com.sava.teachernet.service.TeacherServiceTest.testGetAll"
```

## Environment Variables

The app requires these env vars to start:

| Variable | Purpose |
|---|---|
| `DATABASE_URL` | PostgreSQL JDBC URL |
| `DB_USERNAME` | DB username |
| `DB_PASSWORD` | DB password |
| `GITHUB_CLIENT_ID` / `GITHUB_CLIENT_SECRET` | GitHub OAuth2 app credentials |
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | Google OAuth2 app credentials |

Tests use an in-memory H2 database — no external DB needed for `./gradlew test`.

## Architecture

**Spring Boot 3 / Java 23 MVC app** with Thymeleaf templates, Spring Security, and PostgreSQL.

### Domain Model

Three core JPA entities:

- `User` — authentication record (`login`, `password`, `role` as string)
- `AbstractUser` — mapped superclass holding shared profile fields (`name`, `lastName`, `location`, `dateJoined`); has a `@OneToOne` to `User`
- `Teacher extends AbstractUser` — adds `subject`; has `@ManyToMany(mappedBy)` to `Student`
- `Student extends AbstractUser` — owns the `@ManyToMany` join table `teacher_student`

A `User` is the Spring Security principal; `Teacher`/`Student` are the profile entities linked via `user_id`.

### Authentication & Authorization

Two auth paths both go through `SecurityConfig`:
1. **Form login** — `AuthController` + `AuthService` (implements `UserDetailsService`). Sign-up creates a `User` then a `Teacher` or `Student` record.
2. **OAuth2** (GitHub, Google) — `CustomOAuth2UserService` creates a `User` with role `ROLE_PENDING_OAUTH2_REGISTRATION` on first login. `OAuth2RegistrationController` then prompts role selection; `OAuth2RegistrationService.processRoleSelection` assigns the role and creates the profile entity. `AuthService.refreshAuthentication()` re-issues the `SecurityContext` after role assignment.

`CustomAuthenticationSuccessHandler` redirects after login based on the assigned role.

**Role enum** (`UserRole`): `STUDENT`, `TEACHER`, `ROLE_PENDING_OAUTH2_REGISTRATION`.

URL authorization is role-based: `/students/**` requires `ROLE_STUDENT`, `/teachers/**` requires `ROLE_TEACHER`.

### Controllers

| Controller | Path | Purpose |
|---|---|---|
| `AuthController` | `/auth/**` | Sign-in / sign-up forms |
| `TeacherNetController` | `/`, `/teacher-portal` | Welcome page & role redirect |
| `TeacherController` | `/teachers/**` | Teacher list, search, dashboard, profile, students |
| `StudentController` | `/students/**` | Student list, dashboard, profile, teachers |
| `OAuth2RegistrationController` | `/oauth2/registration` | Post-OAuth2 role selection |

### Search

`TeacherSpecs` builds JPA `Specification<Teacher>` predicates from a `SearchDto` (name, lastName, subject, location — all optional, all case-insensitive LIKE). `TeacherRepository` extends `JpaSpecificationExecutor`.

### DTO / Mapping

MapStruct mappers (`TeacherMapper`, `StudentMapper`) convert entities to DTOs. Mappers are Spring beans (`componentModel = "spring"`).

### Database Migrations

Liquibase; master changelog at `src/main/resources/db/changelog/db.changelog-master.yaml` includes all files under `migrations/` by prefix order (numbered 1–8).
