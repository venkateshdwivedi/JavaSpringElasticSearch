## 1. Overview

This Spring Boot application integrates with Elasticsearch (version 7.17.0) to provide:

- Full-text course search with filters, pagination, and sorting
- Autocomplete suggestions using Elasticsearch’s Completion Suggester
- Fuzzy matching support to tolerate search typos

The application indexes a sample dataset of 60 courses on startup. Each course contains fields such as:

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
- Spring Boot 2.7.18  
- Maven  
- Docker and Docker Compose
- Elasticsearch 7.17.0 (automatically handled via Docker)

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
http://localhost:9200
```

```json
{
  "name": "ddb33b43eb60",
  "cluster_name": "docker-cluster",
  "cluster_uuid": "HZWhpXq4Twq_a1jAaS-MEg",
  "version": {
    "number": "7.17.0",
    "build_flavor": "default",
    "build_type": "docker",
    "build_hash": "bee86328705acaa9a6daede7140defd4d9ec56bd",
    "build_date": "2022-01-28T08:36:04.875279988Z",
    "build_snapshot": false,
    "lucene_version": "8.11.1",
    "minimum_wire_compatibility_version": "6.8.0",
    "minimum_index_compatibility_version": "6.0.0-beta1"
  },
  "tagline": "You Know, for Search"
}
```




---

## 4. Build and Run the Spring Boot Application

```bash
mvn spring-boot:run
```

This will start the application at `http://localhost:8080` and automatically index data from sample-courses.json.

## 5. Populating the Index
On startup, the application reads sample-courses.json from the src/main/resources/ directory using a @PostConstruct method in CourseLoaderService.
Each course is indexed into the courses index in Elasticsearch.

### 5.1 How to Verify Data Indexing

On application startup, the class `CourseLoaderService` runs automatically (using `@PostConstruct`), which:

- Reads the file `sample-courses.json` from `src/main/resources/`
- Converts each JSON object to a `CourseDocument`
- Sets up autocomplete `suggest` field
- Saves all course documents to the `courses` index in Elasticsearch

To verify that indexing was successful, you can run this HTTP command:

```bash
http://localhost:9200/courses/_count
```

```json
{
  "count": 60,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  }
}
```

This confirms that the courses were successfully ingested into Elasticsearch.

## 6. Course Search API
### 6.1 Endpoint
`GET /api/search`

### 6.2 Query Parameters

### 6.2 Query Parameters

```text
q            → Full-text search on title and description
category     → Filter by course category (exact match)
type         → ONE_TIME, COURSE, or CLUB
minAge       → Minimum age filter
maxAge       → Maximum age filter
minPrice     → Minimum price filter
maxPrice     → Maximum price filter
startDate    → ISO format date (e.g., 2025-08-01)
sort         → Sort field: nextSessionDate, priceAsc, priceDesc
order        → asc or desc (default is asc)
page         → Page number (default: 0)
size         → Number of results per page (default: 10)
```

## 7. Example Queries (Search API)

The following examples demonstrate different combinations of parameters and expected behaviors. You can run these using `curl` or browser using HTTP request.

### 7.1 Basic Full-Text Search

```bash
http://localhost:8080/api/search?q=math
```
Expected behavior:
- Returns courses where 'title' or 'description' contains 'math'
- Fuzzy matching enabled

```json
{
  "total": 1,
  "courses": [
    {
      "id": "course16",
      "title": "Math Puzzles",
      "description": "This is a course about math puzzles designed to engage and educate students.",
      "category": "Math",
      "type": "COURSE",
      "gradeRange": "6th–8th",
      "minAge": 11,
      "maxAge": 14,
      "price": 80.19,
      "nextSessionDate": "2025-08-14T14:38:35Z"
    }
  ]
}
```
### 7.2 Filter by Category and Type
```bash
http://localhost:8080/api/search?category=Science&type=CLUB
```
Expected behavior:
- Only courses where category == 'Science' and type == 'CLUB'

```json
{
  "total": 7,
  "courses": [
    {
      "id": "course9",
      "title": "Physics in Real Life",
      "description": "This is a course about physics in real life designed to engage and educate students.",
      "category": "Science",
      "type": "CLUB",
      "gradeRange": "6th–8th",
      "minAge": 11,
      "maxAge": 14,
      "price": 198.83,
      "nextSessionDate": "2025-07-18T14:38:35Z"
    },
    {
      "id": "course43",
      "title": "Rocket Science Lite",
      "description": "This is a course about rocket science lite designed to engage and educate students.",
      "category": "Science",
      "type": "CLUB",
      "gradeRange": "9th–12th",
      "minAge": 14,
      "maxAge": 18,
      "price": 97.1,
      "nextSessionDate": "2025-07-18T14:38:35Z"
    },

```

### 7.3 Age Range Filtering

```bash
http://localhost:8080/api/search?minAge=6&maxAge=10
```
Expected behavior:
- Courses where maxAge >= 6 and minAge <= 10

```json
{
  "total": 30,
  "courses": [
    {
      "id": "course37",
      "title": "Digital Photography",
      "description": "This is a course about digital photography designed to engage and educate students.",
      "category": "General",
      "type": "CLUB",
      "gradeRange": "4th–6th",
      "minAge": 9,
      "maxAge": 12,
      "price": 339.67,
      "nextSessionDate": "2025-07-16T14:38:35Z"
    },
    {
      "id": "course20",
      "title": "Introduction to French",
      "description": "This is a course about introduction to french designed to engage and educate students.",
      "category": "Language",
      "type": "COURSE",
      "gradeRange": "4th–6th",
      "minAge": 9,
      "maxAge": 12,
      "price": 190.3,
      "nextSessionDate": "2025-07-18T14:38:35Z"
},

```
### 7.4 Price Range and Sorting by Price Ascending

```bash
http://localhost:8080/api/search?minPrice=50&maxPrice=200&sort=price&order=asc
```
Expected behavior:
- Returns courses priced between 50 and 200
- Sorted by price in ascending order

```json
{
  "total": 24,
  "courses": [
    {
      "id": "course29",
      "title": "Learn Origami",
      "description": "This is a course about learn origami designed to engage and educate students.",
      "category": "Art",
      "type": "ONE_TIME",
      "gradeRange": "9th–12th",
      "minAge": 14,
      "maxAge": 18,
      "price": 65.35,
      "nextSessionDate": "2025-07-31T14:38:35Z"
    },
    {
      "id": "course16",
      "title": "Math Puzzles",
      "description": "This is a course about math puzzles designed to engage and educate students.",
      "category": "Math",
      "type": "COURSE",
      "gradeRange": "6th–8th",
      "minAge": 11,
      "maxAge": 14,
      "price": 80.19,
      "nextSessionDate": "2025-08-14T14:38:35Z"
    },
```

### 7.5 Start Date Filter and Pagination

```bash
http://localhost:8080/api/search?startDate=2025-08-01&page=0&size=5
```
Expected behavior:
- Courses with nextSessionDate >= 2025-08-01
- First page (0), max 5 results

```json
{
  "total": 44,
  "courses": [
    {
      "id": "course3",
      "title": "World History",
      "description": "This is a course about world history designed to engage and educate students.",
      "category": "General",
      "type": "CLUB",
      "gradeRange": "9th–12th",
      "minAge": 14,
      "maxAge": 18,
      "price": 496.7,
      "nextSessionDate": "2025-08-01T14:38:35Z"
    },
    {
      "id": "course2",
      "title": "Intro to Biology",
      "description": "This is a course about intro to biology designed to engage and educate students.",
      "category": "Science",
      "type": "CLUB",
      "gradeRange": "1st–3rd",
      "minAge": 6,
      "maxAge": 9,
      "price": 284.85,
      "nextSessionDate": "2025-08-04T14:38:35Z"
    },
    {
      "id": "course55",
      "title": "Augmented Reality Basics",
      "description": "This is a course about augmented reality basics designed to engage and educate students.",
      "category": "Technology",
      "type": "COURSE",
      "gradeRange": "6th–8th",
      "minAge": 11,
      "maxAge": 14,
      "price": 82.97,
      "nextSessionDate": "2025-08-04T14:38:35Z"
    },
    {
      "id": "course42",
      "title": "Build a Website",
      "description": "This is a course about build a website designed to engage and educate students.",
      "category": "Technology",
      "type": "ONE_TIME",
      "gradeRange": "4th–6th",
      "minAge": 9,
      "maxAge": 12,
      "price": 171.55,
      "nextSessionDate": "2025-08-06T14:38:35Z"
    },
    {
      "id": "course11",
      "title": "Space Science",
      "description": "This is a course about space science designed to engage and educate students.",
      "category": "Science",
      "type": "CLUB",
      "gradeRange": "9th–12th",
      "minAge": 14,
      "maxAge": 18,
      "price": 370.38,
      "nextSessionDate": "2025-08-08T14:38:35Z"
    }
  ]
}

```

### 7.6 Combined Query (Text + Filters + Sorting)

```bash
http://localhost:8080/api/search?category=Math&type=COURSE&minAge=7&sort=price&order=asc
```
Expected behavior:
- Returns Math courses of type COURSE with minAge <= 7 and maxAge >= 7
- Sorted by price ascending

```json
{
  "total": 1,
  "courses": [
    {
      "id": "course16",
      "title": "Math Puzzles",
      "description": "This is a course about math puzzles designed to engage and educate students.",
      "category": "Math",
      "type": "COURSE",
      "gradeRange": "6th–8th",
      "minAge": 11,
      "maxAge": 14,
      "price": 80.19,
      "nextSessionDate": "2025-08-14T14:38:35Z"
    }
  ]
}
```

## 8. Autocomplete Suggestion API

### 8.1 Endpoint
`GET /api/search/suggest?q={partialTitle}`
This endpoint provides course title suggestions using the Elasticsearch Completion Suggester on the `suggest` field. 
It returns up to 10 results that **start with** the given prefix.

### 8.2 Example Request

```bash
http://localhost:8080/api/search/suggest?q=bui
```
Expected behavior:
- Queries the 'suggest' field using prefix 'bui'
- Returns up to 10 suggested course titles (e.g., Build a Birdhouse)
- Result is a JSON array of strings

```json
[
  "Build a Birdhouse",
  "Build a Robot",
  "Build a Website"
]
```



## 9. Fuzzy Matching (Full-Text Search with Typos)

### 9.1 Endpoint

`GET /api/search?q=matth`

### 9.2 Description

```text
Expected behavior:
- Returns course titles like "Math Puzzles" even though "matth" is a typo
- Fuzzy search is applied to both 'title' and 'description'
- Title field is given higher importance (boosted relevance)
- Matching results with the typo appear ranked higher if the match is in the title
```
### 9.3 Example Request

```bash
http://localhost:8080/api/search?q=matth
```
```json
{
  "total": 1,
  "courses": [
    {
      "id": "course16",
      "title": "Math Puzzles",
      "description": "This is a course about math puzzles designed to engage and educate students.",
      "category": "Math",
      "type": "COURSE",
      "gradeRange": "6th–8th",
      "minAge": 11,
      "maxAge": 14,
      "price": 80.19,
      "nextSessionDate": "2025-08-14T14:38:35Z"
    }
  ]
}
```
