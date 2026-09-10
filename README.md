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
- [x] Shorten long URLs with Base62 encoding
- [x] Retrieve original URL from short code
- [x] MySQL persistent storage
- [ ] Collision handling
- [ ] URL validation
- [ ] Redis caching
- [ ] Rate limiting
- [ ] Docker deployment

## 📡 API Endpoints
| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | /api/health | Health check | ✅ |
| POST | /api/urls | Shorten a URL | ✅ |
| GET | /api/urls/{shortCode} | Get original URL | ✅ |

## 📦 Request/Response

### POST /api/urls
**Request:**
```json
{
    "originalUrl": "https://www.google.com"
}
```

**Response:**
```json
{
    "originalUrl": "https://www.google.com",
    "shortCode": "vG9BvE",
    "shortUrl": "http://localhost:8080/vG9BvE"
}
```

## 🏗️ Project Structure
