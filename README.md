# 🔗 Distributed URL Shortener

A production-conscious **URL shortening backend** built with **Java, Spring Boot, MySQL, Redis, and Docker**.

The system converts long URLs into compact short links, performs HTTP redirects, persists URL mappings in MySQL, uses Redis for fast in-memory operations and rate limiting, and runs the complete application stack using Docker Compose.

> Built as a backend engineering project with emphasis on API design, caching, persistence, rate limiting, containerization, and service-to-service communication.

---

## 🚀 Features

- 🔗 Generate short URLs from long URLs
- ↪️ Redirect short URLs to their original destination
- 💾 Persistent URL storage using MySQL
- ⚡ Redis integration
- 🛡️ Redis-backed IP rate limiting
- ✅ Request validation
- 🚫 Proper error handling for invalid or missing short codes
- 🌐 `X-Forwarded-For` aware client IP detection
- 🐳 Dockerized Spring Boot application
- 🧩 Multi-container setup using Docker Compose
- ❤️ MySQL health checks before application startup
- 🔐 Configuration through environment variables
- 💽 Docker volume for persistent MySQL data
- ☁️ Railway-ready container configuration

---

## 🏗️ Architecture

```text
                         Client / Postman
                               │
                               │ HTTP
                               ▼
                    ┌─────────────────────┐
                    │   Spring Boot API   │
                    │      Port 8080      │
                    └──────────┬──────────┘
                               │
                 ┌─────────────┴─────────────┐
                 │                           │
                 ▼                           ▼
        ┌─────────────────┐         ┌─────────────────┐
        │      MySQL      │         │      Redis      │
        │                 │         │                 │
        │ URL Persistence │         │ Rate Limiting   │
        │ Port 3306       │         │ Port 6379       │
        └─────────────────┘         └─────────────────┘
```

Inside Docker, the application communicates with infrastructure using **Docker service names**:

```text
Spring Boot → mysql:3306
Spring Boot → redis:6379
```

The application does **not** depend on `localhost` when running inside Docker.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot |
| REST API | Spring Web |
| Persistence | Spring Data JPA / Hibernate |
| Database | MySQL 8 |
| Cache / Rate Limit Store | Redis 7 |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |
| Containerization | Docker |
| Orchestration | Docker Compose |
| Deployment | Railway |
| API Testing | Postman |

---

## 🔄 Request Flow

### Short URL Creation

```text
Client
  │
  │ POST /api/urls/shorten
  ▼
UrlController
  │
  ▼
UrlService
  │
  ├── Generate short code
  │
  └── Save mapping
          │
          ▼
        MySQL
```

Example:

```text
https://www.google.com
        │
        ▼
     BKNdZh
        │
        ▼
http://localhost:8080/BKNdZh
```

### Redirect Flow

```text
Client
  │
  │ GET /BKNdZh
  ▼
RedirectController
  │
  ▼
UrlService
  │
  ▼
Retrieve original URL
  │
  ▼
HTTP 302 Found
  │
  ▼
https://www.google.com
```

---

## 📡 API Endpoints

### Create Short URL

```http
POST /api/urls/shorten
```

Request:

```json
{
  "originalUrl": "https://www.google.com"
}
```

Example response:

```json
{
  "originalUrl": "https://www.google.com",
  "shortCode": "BKNdZh",
  "shortUrl": "http://localhost:8080/BKNdZh"
}
```

Status:

```http
201 Created
```

---

### Redirect to Original URL

```http
GET /{shortCode}
```

Example:

```http
GET /BKNdZh
```

Response:

```http
302 Found
Location: https://www.google.com
```

---

## ✅ Validation & Error Handling

The API validates incoming requests and returns appropriate HTTP status codes.

| Scenario | Status |
|---|---:|
| Valid URL creation | `201 Created` |
| Successful redirect | `302 Found` |
| Unknown short code | `404 Not Found` |
| Empty request body | `400 Bad Request` |
| Blank URL | `400 Bad Request` |
| Invalid URL scheme | `400 Bad Request` |
| Malformed JSON | `400 Bad Request` |
| Rate limit exceeded | `429 Too Many Requests` |

Validation and application exceptions are handled centrally using a global exception handler.

---

## 🛡️ Rate Limiting

The service implements **Redis-backed IP rate limiting**.

Current policy:

```text
Maximum requests : 10
Window           : 1 minute
Key format       : rate_limit:<client-ip>
```

Flow:

```text
Incoming Request
       │
       ▼
RateLimitInterceptor
       │
       ▼
Identify Client IP
       │
       ▼
Redis INCR rate_limit:<ip>
       │
       ├── count <= 10 ──→ Allow request
       │
       └── count > 10 ───→ HTTP 429
```

When the limit is exceeded:

```http
429 Too Many Requests
Retry-After: 60
```

Example response:

```json
{
  "status": 429,
  "message": "Too many requests. Please try again after 1 minute.",
  "timestamp": "..."
}
```

The interceptor also checks `X-Forwarded-For`, which is important when the application runs behind a reverse proxy or cloud platform.

---

## 🐳 Docker Architecture

The local environment contains three services:

```text
┌───────────────────────────────────────────┐
│              Docker Network               │
│                                           │
│  ┌────────────┐                           │
│  │    app     │                           │
│  │ Spring Boot│                           │
│  └─────┬──────┘                           │
│        │                                  │
│        ├──────────→ mysql:3306            │
│        │                                  │
│        └──────────→ redis:6379            │
│                                           │
│  ┌────────────┐      ┌────────────┐       │
│  │   MySQL    │      │   Redis    │       │
│  └────────────┘      └────────────┘       │
└───────────────────────────────────────────┘
```

Docker Compose automatically creates an internal network where services can resolve each other using their service names.

---

## 🐳 Dockerfile

The project uses a **multi-stage Docker build**.

### Stage 1 — Build

```dockerfile
FROM maven:3.9.9-eclipse-temurin-17 AS build
```

Maven compiles the project and generates the executable Spring Boot JAR.

### Stage 2 — Runtime

```dockerfile
FROM eclipse-temurin:17-jre
```

Only the Java runtime and final application JAR are copied into the production image.

This keeps build tooling out of the runtime image.

---

## 🧩 Docker Compose

Docker Compose starts:

```text
Spring Boot
MySQL
Redis
```

with a single command:

```bash
docker compose up --build
```

MySQL also has a health check configured so the application waits until the database becomes available before starting.

---

## 💽 Persistent Database Storage

MySQL uses a named Docker volume:

```yaml
volumes:
  - mysql_data:/var/lib/mysql
```

This separates the lifecycle of the database data from the lifecycle of the MySQL container.

```text
MySQL Container
      │
      ▼
 mysql_data
 Docker Volume
```

The container can therefore be recreated without automatically deleting the stored database data.

---

## 🔐 Environment Variables

Secrets and deployment-specific configuration are not hardcoded into the application.

Required variables:

```env
MYSQL_ROOT_PASSWORD=your_root_password

DB_HOST=mysql
DB_PORT=3306
DB_NAME=urlshortener
DB_USERNAME=urluser
DB_PASSWORD=your_database_password

REDIS_HOST=redis
REDIS_PORT=6379
```

For cloud deployment, the server port can also be provided dynamically:

```env
PORT=8080
```

Spring Boot configuration:

```properties
server.port=${PORT:8080}

spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:urlshortener}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}

spring.data.redis.host=${REDIS_HOST:localhost}
spring.data.redis.port=${REDIS_PORT:6379}
```

This allows the same codebase to run both locally and inside containers/cloud environments.

---

## ⚙️ Running Locally with Docker

### Prerequisites

Install:

```text
Docker Desktop
Git
```

Clone the repository:

```bash
git clone https://github.com/karan-sahani-dev/url-shortener.git
```

Move into the project:

```bash
cd url-shortener
```

Create a `.env` file:

```env
MYSQL_ROOT_PASSWORD=root123
DB_NAME=urlshortener
DB_USERNAME=urluser
DB_PASSWORD=urlpass123
```

Then start the complete stack:

```bash
docker compose up --build
```

The API will be available at:

```text
http://localhost:8080
```

To stop the application:

```bash
docker compose down
```

To remove containers **and volumes**:

```bash
docker compose down -v
```

> `-v` deletes the database volume as well, so persisted MySQL data will be removed.

---

## 🧪 Testing with Postman

Create a short URL:

```http
POST http://localhost:8080/api/urls/shorten
```

Body:

```json
{
  "originalUrl": "https://www.google.com"
}
```

Then use the generated code:

```http
GET http://localhost:8080/{shortCode}
```

Example:

```http
GET http://localhost:8080/BKNdZh
```

Expected:

```http
302 Found
Location: https://www.google.com
```

---

## 📁 Project Structure

```text
url-shortener/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/karan/url_shortener/
│       │       ├── Controller/
│       │       │   ├── UrlController.java
│       │       │   └── RedirectController.java
│       │       │
│       │       ├── DTO/
│       │       ├── entity/
│       │       ├── exception/
│       │       ├── interceptor/
│       │       ├── repository/
│       │       ├── service/
│       │       └── UrlShortenerApplication.java
│       │
│       └── resources/
│           └── application.properties
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── .gitignore
└── README.md
```

---

## 🧠 Engineering Decisions

### Why MySQL?

URL mappings require durable storage.

```text
shortCode → originalUrl
```

MySQL provides persistence, indexing, uniqueness constraints, and transactional consistency.

### Why Redis?

Redis is suited for short-lived, frequently updated state.

In this project it is used for request-rate counters where operations such as:

```text
INCR
EXPIRE
```

are efficient.

### Why Docker Compose?

The application depends on multiple infrastructure services:

```text
Spring Boot
MySQL
Redis
```

Docker Compose provides a reproducible local environment and lets the entire stack start with one command.

### Why environment variables?

Database credentials and infrastructure addresses vary between:

```text
Local development
Docker
Cloud deployment
```

Environment variables keep infrastructure configuration outside application code.

### Why MySQL health checks?

`depends_on` alone does not guarantee that MySQL is ready to accept connections.

The health check ensures this startup order:

```text
MySQL container starts
        ↓
MySQL becomes healthy
        ↓
Spring Boot starts
```

---

## 🔥 Problems Solved During Development

This project also involved debugging real integration issues such as:

```text
Docker host port conflicts
Container-name conflicts
localhost vs Docker service networking
MySQL startup race conditions
Database authentication failures
Redis port conflicts
Docker Compose YAML indentation
Environment-variable configuration
Persistent volumes
Container health checks
Multi-stage Docker builds
```

These issues helped turn the project from a basic CRUD API into a more realistic backend deployment exercise.

---

## 📈 Current Behavior

Calling the shortening endpoint multiple times with the same original URL currently generates a new short code each time.

Example:

```text
google.com → AbC123
google.com → XyZ987
```

Deduplication can be introduced later depending on product requirements.

---

## 🚧 Future Improvements

```text
├── Custom aliases
├── URL expiration / TTL
├── Click analytics
├── Per-user rate limits
├── Authentication & API keys
├── Cached URL lookups
├── Duplicate URL deduplication
├── Distributed ID generation
├── Kafka-based analytics pipeline
├── Horizontal scaling
├── Observability with metrics and tracing
└── CI/CD pipeline
```

---

## ☁️ Deployment

The application is designed to be deployable on Railway using:

```text
Spring Boot Docker Service
        │
        ├── Railway MySQL
        │
        └── Railway Redis
```

Live deployment:

```text
Coming soon...
```

Once deployed, this section will contain the public API URL.

---

## 🎯 What This Project Demonstrates

This project focuses on backend concepts beyond basic CRUD:

```text
REST API design
      │
      ├── Validation
      ├── Exception handling
      ├── HTTP redirects
      │
Database layer
      │
      ├── MySQL
      ├── JPA / Hibernate
      │
Redis
      │
      └── Rate limiting
      │
Infrastructure
      │
      ├── Docker
      ├── Docker Compose
      ├── Networking
      ├── Health checks
      ├── Volumes
      └── Environment variables
```

---

## 👨‍💻 Author

**Karan Sahani**

Backend / Software Engineer focused on:

```text
Java
Spring Boot
Backend Engineering
Distributed Systems
Data Structures & Algorithms
```

GitHub:

[github.com/karan-sahani-dev](https://github.com/karan-sahani-dev)

---

## ⭐ Support

If you found this project useful or interesting, consider giving the repository a ⭐.
