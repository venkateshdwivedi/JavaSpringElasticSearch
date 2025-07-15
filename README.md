## 1. Overview

This Spring Boot application integrates with Elasticsearch (version 7.17.0) to provide:

- Full-text course search with filters, pagination, and sorting
- Autocomplete suggestions using Elasticsearch’s Completion Suggester
- Fuzzy matching support to tolerate search typos

The application indexes a sample dataset of courses on startup. Each course contains fields such as:

- `title`: Course title (used for search and autocomplete)
- `description`: Brief course summary
- `category`: Subject area (e.g., Math, Science, Art)
- `type`: Delivery format (`ONE_TIME`, `COURSE`, `CLUB`)
- `gradeRange`: e.g., "1st–3rd", "9th–12th"
- `minAge` and `maxAge`: Target age range
- `price`: Enrollment cost
- `nextSessionDate`: Date of next available session

REST APIs are provided to query these fields via filters, full-text search, and suggestions.

## 2. Prerequisites

- Java 21  
- Maven  
- Docker and Docker Compose

---

## 3. Launch Elasticsearch

### 3.1 Launch using Docker Compose

```bash
docker-compose up -d
```

### 3.2 Verify Elasticsearch is Running

```bash
curl http://localhost:9200
```

---

## 4. Build and Run the Spring Boot Application


