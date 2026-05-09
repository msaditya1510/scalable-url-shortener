# Routr

A backend-focused URL shortener built using Java and Spring Boot, featuring Redis-based caching, analytics tracking, and a lightweight React frontend for link management.

## Features

- Generate short URLs using Base62 encoding
- Redirect users to original URLs
- Redis cache-aside strategy for faster lookups
- URL click analytics
- Redis-to-database scheduled synchronization
- Lightweight frontend for URL shortening and analytics lookup
- Global exception handling
- Unit and integration testing

## Tech Stack

### Backend
- Java
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Redis
- JUnit & Mockito
- MockMvc

### Frontend
- React
- Vite
- Tailwind CSS

## Architecture Overview

```text
Client
   ↓
React Frontend
   ↓
Spring Boot API
   ↓
Redis Cache + Analytics
   ↓
PostgreSQL
```

### URL Redirection Flow

1. User requests a short URL
2. Application first checks Redis cache
3. If cache miss occurs, data is fetched from PostgreSQL
4. Retrieved data is cached for future requests
5. User is redirected to the original URL

### Analytics Flow

- Click counts are temporarily stored in Redis
- A scheduled job periodically syncs analytics data to PostgreSQL
- Total clicks are calculated using both Redis and database values

## Project Structure

```text
.
├── frontend/        # React + Vite frontend
├── src/             # Spring Boot backend
├── Dockerfile
├── pom.xml
└── README.md
```

## API Endpoints

### Create Short URL

```http
POST /shorten?url=https://example.com
```

Example Response:

```text
abc123
```

### Redirect URL

```http
GET /{shortCode}
```

Redirects the user to the original URL.

### Get URL Statistics

```http
GET /stats/{shortCode}
```

Example Response:

```json
{
  "shortCode": "abc123",
  "clicks": 15
}
```

## Running the Project

### Prerequisites

- Java 21+
- Maven
- Redis instance
- PostgreSQL database
- Node.js

### Clone the Repository

```bash
git clone https://github.com/msaditya1510/scalable-url-shortener.git
cd scalable-url-shortener
```

## Backend Setup

### Configure Environment Variables

```properties
DB_URL=
DB_USERNAME=
DB_PASSWORD=

REDIS_HOST=
REDIS_PORT=
REDIS_PASSWORD=
```

### Run Backend

```bash
./mvnw spring-boot:run
```

Backend runs on:

```text
http://localhost:8080
```

## Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on:

```text
http://localhost:5173
```

## Testing

The backend includes:

- Unit tests using Mockito
- Integration tests using MockMvc

Run tests using:

```bash
./mvnw test
```

## Deployment

- Backend deployed on Render using Docker
- Frontend deployed on Vercel
- PostgreSQL hosted on Supabase
- Redis hosted on Redis Cloud

## Design Decisions

- Base62 encoding is used to generate short and collision-free URLs
- Redis cache-aside strategy reduces database load for frequently accessed URLs
- Analytics updates are first written to Redis and later synchronized to PostgreSQL using a scheduled task
- Frontend short URLs are routed through deployment rewrites while backend handles redirect resolution

## Current Limitations

- Analytics are based on raw HTTP requests, so browser prefetching may slightly affect counts
- Current Redis key scanning approach is not optimized for very large scale
- The analytics pipeline follows eventual consistency because Redis and database synchronization happens periodically

## Future Improvements

- GitHub Actions workflow to periodically ping deployments and reduce cold starts
- Custom aliases for shortened URLs
- Rate limiting and abuse protection
- Expiration support for temporary URLs