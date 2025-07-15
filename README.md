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

### 3.2 Elasticsearch Configuration

The application is configured to connect to Elasticsearch running at `http://localhost:9200`.

This is set in `src/main/resources/application.properties`:

```properties
spring.application.name=springbootelasticsearch
spring.elasticsearch.uris=http://localhost:9200
```
No authentication is required, and no further changes are needed if you're using the provided `docker-compose.yml` file.

Ensure Docker is running and Elasticsearch is up at `localhost:9200` before starting the application.


### 3.3 Verify Elasticsearch is Running

```bash
curl http://localhost:9200
```

<pre><code>```json { "name": "ddb33b43eb60", "cluster_name": "docker-cluster", "cluster_uuid": "HZWhpXq4Twq_a1jAaS-MEg", "version": { "number": "7.17.0", "build_flavor": "default", "build_type": "docker", "build_hash": "bee86328705acaa9a6daede7140defd4d9ec56bd", "build_date": "2022-01-28T08:36:04.875279988Z", "build_snapshot": false, "lucene_version": "8.11.1", "minimum_wire_compatibility_version": "6.8.0", "minimum_index_compatibility_version": "6.0.0-beta1" }, "tagline": "You Know, for Search" } ```</code></pre>



---

## 4. Build and Run the Spring Boot Application

```bash
mvn spring-boot:run
```

This will start the application at `http://localhost:8080` and automatically index data from sample-courses.json.

## 5. Populating the Index
