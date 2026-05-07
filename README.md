# Clarity — Focus & Study Room Backend

A backend service for a productivity app that combines personal focus session tracking with real-time collaborative study rooms.

Built with **Java 21 + Spring Boot 4**, backed by **PostgreSQL**, **Redis**, and **WebSocket (STOMP)**.

---

## Features

### Focus Sessions
- Start, complete, or abandon a focus session (optionally linked to a task)
- Prevents concurrent sessions — only one active session per user at a time
- Session history retrieval

### Task Management
- Full CRUD for personal tasks with labels
- Tasks cannot be started in a focus session if already completed

### Streak Tracking
- Automatically updates on session completion
- Tracks current streak and longest streak with consecutive-day detection
- Resets streak if user misses a day

### Productivity Statistics
- Daily stats: total focus minutes, session count, completed tasks
- Weekly stats: 7-day breakdown for chart/graph rendering on frontend

### Study Rooms (Real-time)
- Create rooms with unique invite codes
- Join/leave rooms
- Room session lifecycle: `IDLE` → `FOCUSING` → `BREAK` → `IDLE`
- State changes broadcast to all room members via WebSocket (STOMP)
- Room state persisted in Redis for fast access

### Authentication & Security
- JWT-based stateless authentication
- Access token + refresh token rotation
- Redis-based token blacklisting for secure logout
- WebSocket connections authenticated via JWT interceptor

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4 |
| ORM | Spring Data JPA (Hibernate) |
| Security | Spring Security + JJWT |
| Real-time | Spring WebSocket (STOMP) |
| Database | PostgreSQL |
| Cache / State | Redis |
| Migration | Flyway |
| Build | Maven |

---

## Project Structure

```
src/main/java/com/clarity/backend/
├── config/          # Security, WebSocket configuration
├── controller/      # REST controllers + WebSocket message handlers
├── dto/             # Request/Response objects
├── exception/       # Global exception handler
├── model/           # JPA entities
├── repository/      # Spring Data JPA repositories
├── security/        # JWT filter, WebSocket auth interceptor
└── service/         # Business logic layer
```

---

## Database Schema

```
users
├── id (UUID PK)
├── email (unique)
├── password_hash
├── display_name
└── avatar_url

tasks
├── id (UUID PK)
├── user_id (FK → users)
├── title, label
└── is_completed

focus_sessions
├── id (UUID PK)
├── user_id (FK → users)
├── task_id (FK → tasks, nullable)
├── started_at, ended_at
├── duration_minutes
└── status: IN_PROGRESS | COMPLETED | ABANDONED

streaks
├── id (UUID PK)
├── user_id (FK → users, unique)
├── current_streak, longest_streak
└── last_focus_date

study_rooms
├── id (UUID PK)
├── owner_id (FK → users)
├── name, invite_code (unique)
└── is_active

room_members
├── id (UUID PK)
├── room_id (FK → study_rooms)
└── user_id (FK → users)

room_sessions
├── id (UUID PK)
├── room_id (FK → study_rooms, unique)
├── status: IDLE | FOCUSING | BREAK
├── started_at
└── duration_minutes
```

---

## Getting Started

### Prerequisites
- Java 21
- PostgreSQL
- Redis

### Environment Variables

Create a `.env` file or set the following environment variables:

```env
DB_URL=jdbc:postgresql://localhost:5432/clarity
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000
```

### Run

```bash
./mvnw spring-boot:run
```

Flyway will automatically run all database migrations on startup.

---

## API Overview

| Module | Base Path |
|---|---|
| Auth | `/api/users` |
| Tasks | `/api/tasks` |
| Focus Sessions | `/api/sessions` |
| Statistics | `/api/stats` |
| Study Rooms | `/api/rooms` |
| WebSocket | `/ws` (STOMP) |

### WebSocket Endpoints (STOMP)

| Destination | Description |
|---|---|
| `/app/room/{roomId}/start` | Start a room focus session |
| `/app/room/{roomId}/break` | Switch room to break |
| `/app/room/{roomId}/end` | End the room session |
| `/topic/room/{roomId}` | Subscribe to room state updates |

Authentication is required for WebSocket connections. Pass the JWT access token as a STOMP connect header.
