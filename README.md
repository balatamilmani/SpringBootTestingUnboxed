# Spring Boot Testing Unboxed: Practical Spring Boot Test Patterns

This project demonstrates a few common testing styles in a Spring Boot application:

- Unit testing a service without starting Spring
- MVC controller testing with `@WebMvcTest`
- Full application testing with `@SpringBootTest` and `MockMvc`
- Real HTTP testing with `@SpringBootTest(webEnvironment = RANDOM_PORT)` and `RestTestClient`
- Standard error responses with Spring's `ProblemDetail`

The examples are based on the Spring Boot testing guidance in the official reference documentation:

- https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html

This repo uses Spring Boot `4.0.6`, so the MVC examples use the Boot 4 testing module and Spring Framework's `@MockitoBean`.

## Demo scenario

The sample application is a tiny `book catalog` API:

- `GET /api/books/{id}` returns one book
- `GET /api/books?topic=testing&practicalOnly=true` returns filtered recommendations

The production flow is intentionally simple so the testing patterns are easy to see:

1. `BookCatalogController` handles HTTP requests
2. `BookCatalogService` contains business logic
3. `BookCatalogRepository` abstracts data access
4. `InMemoryBookCatalogRepository` provides a small built-in dataset for demos
5. `CatalogExceptionHandler` converts domain exceptions into HTTP responses

Packages are now split by responsibility:

- `catalog.controller` for HTTP endpoints
- `catalog.controller.advice` for exception handling and controller advice
- `catalog.service` for business logic
- `catalog.repository` for data access abstractions and implementations
- `catalog.model` for domain data
- `catalog.exception` for domain exceptions

## Project structure

- [BookCatalogController.java](/src/main/java/com/balatamilmani/testingunboxed/catalog/controller/BookCatalogController.java)
- [CatalogExceptionHandler.java](/src/main/java/com/balatamilmani/testingunboxed/catalog/controller/advice/CatalogExceptionHandler.java)
- [BookCatalogService.java](/src/main/java/com/balatamilmani/testingunboxed/catalog/service/BookCatalogService.java)
- [BookCatalogRepository.java](/src/main/java/com/balatamilmani/testingunboxed/catalog/repository/BookCatalogRepository.java)
- [InMemoryBookCatalogRepository.java](/src/main/java/com/balatamilmani/testingunboxed/catalog/repository/InMemoryBookCatalogRepository.java)
- [Book.java](/src/main/java/com/balatamilmani/testingunboxed/catalog/model/Book.java)
- [BookNotFoundException.java](/src/main/java/com/balatamilmani/testingunboxed/catalog/exception/BookNotFoundException.java)

Tests:

- [BookCatalogServiceTest.java](/src/test/java/com/balatamilmani/testingunboxed/catalog/service/BookCatalogServiceTest.java)
- [BookCatalogControllerTest.java](/src/test/java/com/balatamilmani/testingunboxed/catalog/controller/BookCatalogControllerTest.java)
- [BookCatalogApplicationTest.java](/src/test/java/com/balatamilmani/testingunboxed/catalog/integration/BookCatalogApplicationTest.java)
- [BookCatalogApplicationHttpTest.java](/src/test/java/com/balatamilmani/testingunboxed/catalog/integration/BookCatalogApplicationHttpTest.java)

## 1. Unit testing without Spring

File: [BookCatalogServiceTest.java](/src/test/java/com/balatamilmani/testingunboxed/catalog/service/BookCatalogServiceTest.java)

This is the fastest style of test in the project. It does not start the Spring container.

It uses:

- `@ExtendWith(MockitoExtension.class)` to enable Mockito with JUnit 5
- `@Mock` to fake the repository
- `@InjectMocks` to create the real service with mocked dependencies

Why this test is useful:

- It isolates business logic
- It is fast and deterministic
- It is ideal for filtering, sorting, calculations, and exception behavior

What it demonstrates:

- Returning a book from the repository
- Throwing `BookNotFoundException` when the repository is empty
- Filtering and sorting recommendation results

Use this style when you want to test logic in one class and do not need Spring features such as MVC, serialization, configuration, or bean wiring.

## 2. MVC controller testing with `@WebMvcTest`

File: [BookCatalogControllerTest.java](/src/test/java/com/balatamilmani/testingunboxed/catalog/controller/BookCatalogControllerTest.java)

This test starts only the Spring MVC slice, not the whole application.

It uses:

- `@WebMvcTest(BookCatalogController.class)` to load controller-related MVC components
- `MockMvc` to perform requests without starting a real server
- `@MockitoBean` to replace `BookCatalogService` with a Mockito mock inside the Spring context

That gives us a clean separation:

- The controller remains real
- Spring MVC infrastructure remains real
- The service dependency is fake and fully controlled by the test

This is especially useful when:

- The real service talks to a database or remote API
- The service is expensive to run
- You want to force error conditions that are hard to reproduce naturally

If you have used `@MockBean` in older codebases, the main idea is the same, but this project uses the Boot 4 / Spring Framework test support shown in current documentation.

Why this test is useful:

- It verifies request mapping and JSON responses
- It checks status codes and error handling
- It keeps the test focused on the web layer

What it demonstrates:

- Returning JSON for `GET /api/books/{id}`
- Returning `404 Not Found` as an RFC 9457 `ProblemDetail` response when the mocked service throws `BookNotFoundException`
- Passing query parameters from the controller to the service

This is the best choice when you want confidence in your controller behavior but do not want the cost of loading the full application.

## Error response standard

This project uses Spring's `org.springframework.http.ProblemDetail` in the controller advice instead of a custom error DTO.

Why:

- It follows the current Spring standard for REST error responses
- It aligns with RFC 9457 "Problem Details for HTTP APIs"
- It still allows application-specific fields such as `code`

For `BookNotFoundException`, the advice returns a response with standard fields such as:

- `title`
- `status`
- `detail`
- `instance`

And one application-specific field:

- `code`

This gives you a standard response format without losing domain-specific context.

## 3. Full application testing with `@SpringBootTest` and `MockMvc`

File: [BookCatalogApplicationTest.java](/src/test/java/com/balatamilmani/testingunboxed/catalog/integration/BookCatalogApplicationTest.java)

This test loads the full Spring Boot application context.

It uses:

- `@SpringBootTest` to bootstrap the application
- `@AutoConfigureMockMvc` to add MVC testing support on top of the full context

**Key difference from HTTP integration tests**: This test does not start a real web server. Instead, it uses `MockMvc` to dispatch requests directly to the Spring MVC layer within the JVM, bypassing network serialization and server infrastructure.

Why this test is useful:

- It checks real bean wiring
- It proves the controller, service, repository, and exception handler work together
- It catches configuration problems that a slice test would miss

What it demonstrates:

- The real in-memory repository is used
- The request flows through the real application layers
- No service mocking is involved

Use this style when you want broader confidence in the whole application or a larger feature path.

## 4. Real HTTP testing with `RestTestClient`

File: [BookCatalogApplicationHttpTest.java](/src/test/java/com/balatamilmani/testingunboxed/catalog/integration/BookCatalogApplicationHttpTest.java)

This test starts the application on a random port and uses a real HTTP client to call the API.

It uses:

- `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)` to start the server
- `@AutoConfigureRestTestClient` to enable `RestTestClient` injection
- `org.springframework.test.web.servlet.client.RestTestClient` to issue a real HTTP request with fluent assertions

**Key difference from MockMvc tests**: This test starts a real embedded web server on a random port and makes actual HTTP requests over the network, exercising full HTTP serialization, deserialization, and server behavior.

Why this test is useful:

- It verifies actual HTTP request and response behavior
- It exercises serialization, request mapping, filters, and the server port
- It is closer to the real production client experience than `MockMvc`

What it demonstrates:

- The application responds on a real HTTP port
- The controller, service, repository, and exception handler work over HTTP
- Response bodies are deserialized through Spring's HTTP message converters

Use this style when you want the highest confidence that the application works as a real HTTP service.

## Which test should I choose?

- Choose a unit test when you want speed and tight focus on one class
- Choose `@WebMvcTest` when you want to test HTTP behavior without loading the whole app
- Choose `@SpringBootTest` with `MockMvc` when you want to verify the full application wiring and collaboration between layers (no real web server)
- Choose `@SpringBootTest` with `RestTestClient` when you want end-to-end HTTP integration testing (real web server and HTTP requests)

A healthy Spring Boot project usually contains all four. They complement each other rather than compete.

## Running the tests

Run the full suite:

```bash
./gradlew test
```

Run only the service unit test:

```bash
./gradlew test --tests "*BookCatalogServiceTest"
```

Run only the MVC controller slice test:

```bash
./gradlew test --tests "*BookCatalogControllerTest"
```

Run only the full application test:

```bash
./gradlew test --tests "*BookCatalogApplicationTest"
```

Run only the real HTTP integration test:

```bash
./gradlew test --tests "*BookCatalogApplicationHttpTest"
```

## Code coverage reports

Generate JaCoCo coverage reports:

```bash
./gradlew test jacocoTestReport
```

Open the HTML report at `build/reports/jacoco/test/html/index.html`.

## Key takeaways

- Unit tests are best for business rules
- MVC slice tests are best for controllers and JSON contracts
- `@MockitoBean` is a clean way to replace collaborators in Spring-managed tests
- `ProblemDetail` is the preferred Spring-native standard for error responses
- `@SpringBootTest` with `MockMvc` gives confidence in application wiring without a web server
- `@SpringBootTest` with `RestTestClient` provides end-to-end HTTP integration testing with a real web server
- `@SpringBootTest` gives the highest confidence, but it is heavier than a unit or slice test

This repo is set up so you can expand it further with repository tests, REST client tests, or data-layer slice tests such as `@JdbcTest` and `@DataJpaTest`.
