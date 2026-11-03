# IMTPS - Intelligent Task Prioritisation System

IMTPS is a student-focused task management and scheduling application developed as part of the University of Cape Town's Computer Science Capstone Project.

The system helps students organise their responsibilities by prioritising tasks and generating personalised schedules based on deadlines, importance, effort, focus preferences and available time.

## Features

- Task Management: Create, edit, complete, reopen and delete tasks.
- Smart Scheduling: Generate timetables using four different scheduling algorithms.
- Today Dashboard: View current tasks, upcoming activities and unscheduled work.
- Schedule Views: View schedules by day, week or month.
- Focus Sessions: Track work sessions and update task progress.
- Notes: Keep personal notes within your account.
- Insights: View task completion and category breakdowns.
- Custom Settings: Configure availability, scheduling preferences, focus behaviour and category colours.
- User Accounts: Register, log in and keep personal data separate.

## Scheduling Algorithms

IMTPS supports four scheduling strategies:

| Algorithm | Description |
|---|---|
| Earliest Deadline First (EDF) | Prioritises tasks with the earliest deadlines. |
| Weighted Focus | Uses a weighted score based on focus category, importance, urgency and effort. |
| Slack Focus | Considers the time remaining before a deadline after accounting for task duration. |
| Category Balance | Distributes tasks across academic, family, health and leisure categories. |

Each strategy determines task priority, while a separate scheduling engine assigns tasks to available time slots.

## Technology Stack

Frontend
- React 19
- TypeScript
- Vite
- React Router

Backend
- Java
- Java Built-in HTTP Server
- Gson
- REST API

Database
- SQLite
- JDBC

Testing
- JUnit 5

## Architecture

The application follows a layered architecture:

1. User Interface: React frontend for interacting with the application.
2. API Layer: Handles HTTP requests, authentication and validation.
3. Service Layer: Contains task management, scheduling, authentication and application logic.
4. Repository Layer: Handles database operations.
5. Database: SQLite stores application data persistently.

The project uses the **Strategy Design Pattern** to support interchangeable scheduling algorithms.

## Security

Security measures include:
- Hashed passwords.
- Bearer-token authentication.
- Prepared SQL statements.
- Server-side input validation.
- User-specific data access.
- Protected API endpoints.

## Team

Developed by Code Crackers as a collaborative university project.

| Team member | Contributions |
|---|---|
| Sisa Shibane | Task management, Slack Focus and Category Balance strategies. |
| Lawrance Mavimbela | Earliest Deadline First, Weighted Focus, registration and login, React interface and Today Dashboard. |
| Sinenkhosi Matimba | Scheduling engine, time assignment, schedule persistence, rebuild rules and schedule views. |

## Project Context

This project was developed for the University of Cape Town's Computer Science Capstone Project (2026). It evolved from an initial JavaFX prototype into a full-stack web application with persistent storage, user accounts and multiple scheduling strategies.

---

Code Crackers | UCT Computer Science Capstone 2026
