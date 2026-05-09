# Clarity — Focus & Study Room Backend

A backend service for a productivity app combining personal focus session tracking with real-time collaborative study rooms. Designed with a layered architecture (Controller → Service → Repository) following REST and WebSocket best practices.

Built with **Java 21 + Spring Boot**, backed by **PostgreSQL**, **Redis**, and **WebSocket (STOMP)**.

> **Live API:** `https://clarity-api-2dpy.onrender.com`
> 
> **API Docs (Swagger UI):** `https://clarity-api-2dpy.onrender.com/swagger-ui/index.html`
> 
> **GitHub:** [https://github.com/shaorankun/clarity-api](https://github.com/shaorankun/clarity-api)

---

## Features

### Authentication & Security
- JWT-based stateless authentication with access token (1h) + refresh token (7 days)
- Redis-based token blacklisting for secure logout
- WebSocket connections authenticated via STOMP header interceptor
- Auto leave study room on logout

### Task Management
- Full CRUD for personal tasks with optional labels
- Cannot start a focus session on an already-completed task

### Focus Sessions
- Start, complete, or abandon a session — optionally linked to a task
- Prevents concurrent sessions: only one active session per user at a time
- Full session history retrieval

### Streak Tracking
- Automatically updates on session completion
- Tracks current streak and longest streak with consecutive-day detection
- Resets if user misses a day

### Productivity Statistics
- Daily stats: total focus minutes, session count, unique completed tasks
- Weekly stats: 7-day breakdown (index 0 = 6 days ago, index 6 = today)

### Study Rooms (Real-time Collaborative)
- Create rooms with auto-generated unique 6-character invite codes
- Join/leave rooms via invite code
- One user can only be in one room at a time
- Room session lifecycle: `IDLE` → `FOCUSING` → `BREAK` → `IDLE`
- Only room owner can control the timer (start/break/end)
- Auto transfer host to earliest-joined member when owner leaves
- Room is automatically deleted when the last member leaves
- Room state cached in Redis for fast sync when new members join
- State changes broadcast to all members via WebSocket (STOMP)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot |
| ORM | Spring Data JPA (Hibernate) |
| Security | Spring Security + JJWT 0.12.6 |
| Real-time | Spring WebSocket (STOMP protocol) |
| Database | PostgreSQL (Supabase) |
| Cache / State | Redis (Upstash) |
| Migration | Flyway |
| Build | Maven |
| Deployment | Render |

---

## Project Structure

```
src/main/java/com/clarity/backend/
├── config/          # Security, WebSocket, CORS configuration
├── controller/      # REST controllers + WebSocket message handlers
├── dto/             # Request/Response DTOs
├── exception/       # Global exception handler
├── model/           # JPA entities
├── repository/      # Spring Data JPA repositories
├── security/        # JWT filter, WebSocket auth interceptor, SecurityUtils
└── service/         # Business logic layer (including Redis services)
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
├── user_id (FK → users, CASCADE)
├── title, label
├── is_completed
└── created_at

focus_sessions
├── id (UUID PK)
├── user_id (FK → users, CASCADE)
├── task_id (FK → tasks, nullable, CASCADE)
├── started_at, ended_at
├── duration_minutes
└── status: IN_PROGRESS | COMPLETED | ABANDONED

streaks
├── id (UUID PK)
├── user_id (FK → users, unique, CASCADE)
├── current_streak, longest_streak
└── last_focus_date

study_rooms
├── id (UUID PK)
├── owner_id (FK → users, CASCADE)
├── name
├── invite_code (unique, 6 chars)
├── is_active
└── created_at

room_members  ← junction table (users ↔ study_rooms)
├── id (UUID PK)
├── room_id (FK → study_rooms, CASCADE)
├── user_id (FK → users, CASCADE)
└── joined_at

room_sessions  ← OneToOne with study_rooms
├── id (UUID PK)
├── room_id (FK → study_rooms, unique, CASCADE)
├── status: IDLE | FOCUSING | BREAK
├── started_at (nullable)
└── duration_minutes (nullable)
```

### Redis Keys

| Key Pattern | Type | TTL | Purpose |
|---|---|---|---|
| `room:{roomId}` | Hash | None | Room timer state cache |
| `{refreshToken}` | String | 7 days | Token blacklist for logout |

---

## API Reference

All endpoints except Auth require `Authorization: Bearer {accessToken}` header.

### Auth — `/api/auth`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/register` | ❌ | Register new account |
| POST | `/login` | ❌ | Login and receive tokens |
| POST | `/refresh` | ❌ | Get new access token |
| POST | `/logout` | ✅ | Blacklist token + auto leave room |

**Register/Login response:**
```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "id": "uuid",
  "email": "string",
  "displayName": "string",
  "avatarUrl": "string | null"
}
```

### Tasks — `/api/tasks`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/` | Get all tasks for current user |
| POST | `/` | Create a task |
| PUT | `/{id}` | Update a task |
| DELETE | `/{id}` | Delete a task |
| PATCH | `/{id}/complete` | Toggle task completion |

### Focus Sessions — `/api/sessions`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/start` | Start a focus session |
| POST | `/end` | End a focus session |
| GET | `/history` | Get session history |

**Start request:**
```json
{
  "durationMinutes": 25,
  "taskId": "uuid | null"
}
```

**End request:**
```json
{
  "sessionId": "uuid",
  "status": "COMPLETED | ABANDONED"
}
```

### Statistics — `/api/stats`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/streak` | Current and longest streak |
| GET | `/daily` | Today's stats |
| GET | `/weekly` | Last 7 days stats |

### Study Rooms — `/api/rooms`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/` | Create a room |
| POST | `/join` | Join a room by invite code |
| GET | `/{id}` | Get room info + member list |
| GET | `/{id}/state` | Get current timer state from Redis |
| DELETE | `/{id}/leave` | Leave room (auto transfer host if owner) |

**Room response:**
```json
{
  "id": "uuid",
  "ownerId": "uuid",
  "name": "string",
  "inviteCode": "string",
  "isActive": true,
  "members": [
    { "userId": "uuid", "displayName": "string" }
  ]
}
```

**Room state response:**
```json
{
  "roomId": "uuid",
  "status": "IDLE | FOCUSING | BREAK",
  "startedAt": "datetime | null",
  "durationMinutes": "int | null"
}
```

### WebSocket (STOMP)

**Connection:** `ws://your-host/ws`
**Auth:** Pass JWT in STOMP CONNECT header: `Authorization: Bearer {accessToken}`

| Type | Destination | Body | Description |
|---|---|---|---|
| SUBSCRIBE | `/topic/room/{roomId}` | — | Receive room state updates |
| SEND | `/app/room/{roomId}/start` | `{"durationMinutes": 25}` | Start timer (owner only) |
| SEND | `/app/room/{roomId}/break` | `{"durationMinutes": 5}` | Start break (owner only) |
| SEND | `/app/room/{roomId}/end` | `{}` | End session → IDLE (owner only) |

All SEND actions broadcast a `RoomSessionResponse` to all subscribers of `/topic/room/{roomId}`.

---

## Getting Started (Local)

### Prerequisites
- Java 21
- PostgreSQL
- Redis (or Docker)

### 1. Start Redis with Docker
```bash
docker run -d --name redis -p 6379:6379 redis
```

### 2. Environment Variables

Create a `.env` file at project root:

```env
DB_URL=jdbc:postgresql://localhost:5432/clarity
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

JWT_SECRET=your_jwt_secret_min_32_chars
JWT_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000
```

### 3. Run

```bash
./mvnw spring-boot:run
```

Flyway will automatically run all database migrations on startup.

### 4. API Documentation
Once running, visit: `http://localhost:8080/swagger-ui/index.html`

---

## Flutter Integration Notes

- Use `stomp_dart_client` package for WebSocket/STOMP
- On entering a study room: call `GET /rooms/{id}` for room info, then `GET /rooms/{id}/state` to sync current timer
- Timer countdown can be computed client-side: `startedAt + durationMinutes - now`
- After break ends, Flutter should automatically send `/start` to begin next focus session
- On 401 response: use refresh token to get new access token, then retry original request
- On joining a room while already in another: prompt user to confirm, then call `leave` on old room before `join` on new room
