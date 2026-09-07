# 🔗 URL Shortener

A distributed URL shortener built with Java + Spring Boot.

## 🛠️ Tech Stack
- Java 21
- Spring Boot 3.3
- MySQL
- Redis (coming soon)
- Docker (coming soon)

## 🚀 Features
- [x] Health check endpoint
- [ ] URL shortening with Base62 encoding
- [ ] Redis caching
- [ ] Rate limiting
- [ ] Docker deployment

## ▶️ How to Run
```bash
git clone https://github.com/karan-sahani-dev/url-shortener.git
cd url-shortener
./mvnw spring-boot:run
```

## 📡 API Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/health | Health check |
