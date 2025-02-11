# 📖 Bookmark API - Learning Path

This repository contains a **Spring Boot REST API** for managing bookmarks, built as part of my learning journey in **Spring Boot, JPA, Hibernate, REST API design, and testing**.

---

## **📌 What I Learned**

### **1️⃣ Setting Up a Spring Boot Project**
- Created a Spring Boot application using **Spring Initializr**.
- Used **Maven** for dependency management.
- Configured **Spring Boot Starter Web**, **Spring Boot Starter JPA**, and **PostgreSQL** for database management.

### **2️⃣ Designing the Bookmark Entity**
- Defined the `Bookmark` entity with the following attributes:
    - `id` (Primary Key with auto-increment)
    - `title` (Bookmark title)
    - `url` (Bookmark link)
    - `createdAt` (Timestamp of creation)
    - `updatedAt` (Timestamp of last update)
- Used **Jakarta Persistence API (JPA)** annotations to map the entity to a PostgreSQL table.

```java
@Entity
@Table(name = "bookmarks")
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookmarks_id_gen")
    @SequenceGenerator(name = "bookmarks_id_gen", sequenceName = "bookmark_id_seq", allocationSize = 50)
    private Long id;

    @Size(max = 200)
    @NotNull
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Size(max = 500)
    @NotNull
    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;
}
```

---

### **3️⃣ Building the Repository Layer**
- Created a `BookmarkRepository` interface extending `JpaRepository` to perform CRUD operations efficiently.
- Implemented methods for:
    - Fetching all bookmarks sorted by `createdAt` (`findAllByOrderByCreatedAtDesc`)
    - Fetching a bookmark by ID (`findBookmarkById`)

```java
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<BookmarkInfo> findAllByOrderByCreatedAtDesc();
    Optional<BookmarkInfo> findBookmarkById(Long id);
}
```

---

### **4️⃣ Developing the REST API (Controller Layer)**
- Implemented the `BookmarkController` with endpoints for **CRUD operations**:
    - **GET** `/api/bookmarks` → Fetch all bookmarks
    - **GET** `/api/bookmarks/{id}` → Fetch a bookmark by ID
    - **POST** `/api/bookmarks` → Create a new bookmark
    - **PUT** `/api/bookmarks/{id}` → Update an existing bookmark
    - **DELETE** `/api/bookmarks/{id}` → Delete a bookmark

#### **📌 Highlights**
✅ Used **DTOs (Payload classes)** for cleaner request validation.  
✅ Used **`@Valid`** for request validation (`@NotEmpty` constraints).  
✅ Handled **404 errors** using a custom `BookmarkNotFoundException`.  
✅ Implemented **structured JSON error responses**.

```java
@ExceptionHandler(BookmarkNotFoundException.class)
ResponseEntity<Map<String, String>> handleBookmarkNotFound(BookmarkNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("error", e.getMessage()));
}
```

---

### **5️⃣ Writing Unit & Integration Tests**
- Used **JUnit 5 + MockMvc** for testing the API endpoints.
- Created a **separate test class** `BookmarkControllerTest` for testing controllers.

#### **📌 Key Test Cases**
✅ **POST** - Create a new bookmark and verify its response.  
✅ **GET** - Fetch a bookmark and check if it exists.  
✅ **PUT** - Update a bookmark and verify the updated fields.  
✅ **DELETE** - Delete a bookmark and expect `204 No Content`.  
✅ **Error Handling** - Ensure `404 Not Found` is returned when an invalid ID is accessed.

#### **✅ Example Test for POST**
```java
@Test
void testCreateBookmark() throws Exception {
    var payload = new BookmarkController.CreateBookmarkPayload("My Bookmark", "https://example.com");

    mockMvc.perform(post("/api/bookmarks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("My Bookmark"))
            .andExpect(jsonPath("$.url").value("https://example.com"));
}
```

#### **✅ Example Test for DELETE**
```java
@Test
void testDeleteBookmark() throws Exception {
    // First create a bookmark
    var payload = new BookmarkController.CreateBookmarkPayload("Test Bookmark", "https://example.com");

    var result = mockMvc.perform(post("/api/bookmarks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated())
            .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    Bookmark createdBookmark = objectMapper.readValue(responseBody, Bookmark.class);
    Long id = createdBookmark.getId();

    // Now delete it
    mockMvc.perform(delete("/api/bookmarks/" + id))
            .andExpect(status().isNoContent());
}
```

---

### **6️⃣ Database Configuration**
- Used **PostgreSQL** as the database.
- Configured database properties in `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bookmarks_db
spring.datasource.username=youruser
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

### **7️⃣ Running the Application**
#### **Run Locally**
```sh
mvn spring-boot:run
```

#### **Test API Using cURL**
```sh
curl -X POST http://localhost:8080/api/bookmarks -H "Content-Type: application/json" -d '{
  "title": "Spring Boot Guide",
  "url": "https://spring.io/guides"
}'
```

---

## **📌 Challenges Faced & How I Overcame Them**
| **Issue** | **Solution** |
|-----------|-------------|
| ID auto-incremented incorrectly (by 1 instead of 50) | Used `allocationSize = 50` in `@SequenceGenerator` |
| `DELETE` and `PUT` tests failed due to missing data | First created a bookmark, extracted the ID, then tested deletion/updating |
| `404 Not Found` returned for missing bookmarks | Implemented `BookmarkNotFoundException` and handled errors using `@ExceptionHandler` |
| MockMvc test failing for serialization | Used `ObjectMapper` from Jackson to serialize and deserialize JSON |

---

## **🚀 Future Improvements**
- Implement **JWT authentication** for securing the API.
- Add **pagination and sorting** for retrieving bookmarks.
- Implement **caching with Redis** for performance optimization.
- Deploy the API using **Docker and Kubernetes**.

---

## **📌 Final Thoughts**
Through this project, I learned:
✅ How to **build a REST API** using **Spring Boot**.  
✅ How to use **JPA and Hibernate** for database operations.  
✅ How to **write clean, maintainable API endpoints**.  
✅ How to **test APIs** using **JUnit 5 and MockMvc**.  
✅ How to **handle errors gracefully** with proper JSON responses.

This project served as a solid **learning path for backend development** with Spring Boot! 🚀🔥

---

## **💻 Contributing**
Feel free to **fork this project**, suggest improvements, or open issues! 🚀

---

## **📚 References**
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Hibernate ORM](https://hibernate.org/orm/)
- [JUnit 5 Testing](https://junit.org/junit5/docs/current/user-guide/)

---

### **Made with ❤️ by Ishwor**
