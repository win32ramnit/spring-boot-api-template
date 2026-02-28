# Spring Boot 4.0 Template

A production-ready Spring Boot 4.0.3 template for quickly kickstarting enterprise Java applications. Includes security, API documentation, database migrations, and best-practice configurations out of the box.

## Features

- **Spring Boot 4.0.3** with Java 21
- **REST API** with OpenAPI 3 / Swagger UI
- **Spring Security** – API key authentication for protected endpoints, OAuth2/JWT ready
- **JPA + Flyway** – MySQL with schema versioning
- **Actuator** – Health checks and monitoring
- **Global exception handling** – Consistent error responses
- **CORS & security headers** – Production-ready defaults
- **Request validation** – Bean Validation support
- **Development tools** – DevTools, Lombok

## Requirements

- **JDK 21** or higher
- **Gradle 9.x** (wrapper included)
- **MySQL 8+** (or use H2 for local development)

## Quick Start

### 1. Clone and build

```bash
git clone <your-repo-url>
cd template
./gradlew build
```

### 2. Configure database

Create a MySQL database and update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_user
spring.datasource.password=your_password
```

### 3. Run the application

```bash
./gradlew bootRun
```

Or run the JAR:

```bash
java -jar build/libs/my-application.jar
```

### 4. Verify

- **Application:** http://localhost:1947/template
- **Health API:** http://localhost:1947/template/api/v1/health
- **Swagger UI:** http://localhost:1947/template/swagger-ui.html
- **Actuator:** http://localhost:9000/actuator (health, info, etc.)

## Project Structure

```
template/
├── src/
│   ├── main/
│   │   ├── java/com/demo/
│   │   │   ├── DemoApplication.java          # Main entry point
│   │   │   ├── controller/                    # REST controllers
│   │   │   ├── config/                        # Configuration, filters, security
│   │   │   ├── constant/                      # Enums, constants
│   │   │   ├── exception/                     # Exception handling
│   │   │   ├── model/                         # JPA entities
│   │   │   ├── repository/                    # JPA repositories
│   │   │   ├── request/                       # Request DTOs
│   │   │   └── response/                      # Response DTOs
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── db/migration/                  # Flyway migrations
│   │       └── log4j/                         # Optional Log4j2 config
│   └── test/
│       ├── java/                              # Unit & integration tests
│       └── resources/
│           └── application.properties         # Test profile (H2)
├── build.gradle
├── properties.gradle
└── settings.gradle
```

## Configuration

### Application Properties (`demo.*`)

| Property | Description | Default |
|----------|-------------|---------|
| `demo.contentSecurityPolicy` | Content-Security-Policy header | `default-src 'self'` |
| `demo.allowedOrigins` | CORS allowed origins | `https://example.com` |
| `demo.excludedUrls` | URL patterns to exclude | `/api/private` |
| `demo.issuerUri` | JWT issuer URI (OAuth2) | - |
| `demo.securityHeaderName` | API key header name | `Authorization` |
| `demo.apiKey` | API key for key-based auth | - |

### Protected vs Public Endpoints

- **Public:** `/api/v1/health`, `/api/v1/info`, `/actuator/**`, `/swagger-ui/**`
- **Protected** (`/v1/**`): Requires `Authorization` header with valid API key

### Profiles

Set `PROFILE` environment variable to activate profiles (comma-separated):

```bash
export PROFILE=local,dev
```

## Dependencies

| Category | Libraries |
|----------|-----------|
| Web | Spring Web MVC, WebFlux, Jersey |
| Data | Spring Data JPA, Flyway |
| Security | Spring Security, OAuth2 Resource Server |
| API Docs | SpringDoc OpenAPI |
| Utilities | Lombok, Commons IO, Commons Lang3, Jsoup |
| Database | MySQL Connector, H2 (test) |

## Running Tests

Tests use an in-memory H2 database. No MySQL required:

```bash
./gradlew test
```

## Customization Guide

1. **Add your API endpoints**
   - Create controllers in `com.demo.controller`
   - Use `@RestController` and `@RequestMapping`

2. **Add entities**
   - Create JPA entities in `com.demo.model`
   - Add repositories in `com.demo.repository`
   - Add Flyway migration in `db/migration/`

3. **Adjust security**
   - Edit `SecurityConfig.java` for path rules
   - Configure `KeyAuthFilter` or enable JWT in `SecurityConfig`

4. **Update package**
   - Replace `com.demo` with your base package
   - Update `@ComponentScan` in `MvcConfiguration`

## License

MIT or your preferred license.
