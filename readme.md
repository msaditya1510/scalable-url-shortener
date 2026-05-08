# Scalable URL Shortener Service

A backend-focused URL shortener built using Java and Spring Boot. The project is designed to handle fast URL redirection using Redis caching and supports basic analytics tracking for shortened URLs.

## Features

- Generate short URLs using Base62 encoding
- Redirect users to original URLs
- Redis cache-aside strategy for faster lookups
- URL click analytics
- Redis-to-database scheduled synchronization
- Global exception handling
- Unit and integration testing

## Tech Stack

- Java
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Redis
- JUnit & Mockito
- MockMvc

## Architecture Overview

```text
Client
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
3. If cache miss occurs, data is fetched from the database
4. Retrieved data is cached for future requests
5. User is redirected to the original URL

### Analytics Flow

- Click counts are temporarily stored in Redis
- A scheduled job periodically syncs analytics data to PostgreSQL
- Total clicks are calculated using both Redis and database values

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

- Java 17+
- Maven
- Redis instance
- PostgreSQL database

### Clone the Repository

```bash
git clone https://github.com/msaditya1510/scalable-url-shortener
cd scalable-url-shortener
```

### Configure Environment Variables

```properties
DB_URL=
DB_USERNAME=
DB_PASSWORD=

REDIS_HOST=
REDIS_PORT=
REDIS_PASSWORD=
```

### Run the Application

```bash
./mvnw spring-boot:run
```

The application starts on:

```text
http://localhost:9090
```

## Testing

The project includes:

- Unit tests using Mockito
- Integration tests using MockMvc

Run tests using:

```bash
./mvnw test
```

## Design Decisions

- Base62 encoding is used to generate short and collision-free URLs
- Redis cache-aside strategy reduces database load for frequently accessed URLs
- Analytics updates are first written to Redis and later synchronized to PostgreSQL using a scheduled task

## Current Limitations

- Analytics are based on raw HTTP requests, so browser prefetching may slightly affect counts
- Current Redis key scanning approach is not optimized for very large scale
- The analytics pipeline follows eventual consistency because Redis and database synchronization happens periodically
