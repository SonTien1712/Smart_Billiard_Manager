# Spring Boot Backend Configuration Guide

This guide outlines the Spring Boot backend structure needed to support the Billiards Club Management System React frontend.

## Project Structure

```
src/main/java/com/billardmanagement/
├── BillardManagementApplication.java
├── config/
│   ├── SecurityConfig.java
│   ├── CorsConfig.java
│   └── JwtConfig.java
├── controller/
│   ├── AuthController.java
│   ├── AdminController.java
│   ├── CustomerController.java
│   └── StaffController.java
├── service/
│   ├── AuthService.java
│   ├── AdminService.java
│   ├── CustomerService.java
│   ├── StaffService.java
│   └── JwtService.java
├── repository/
│   ├── UserRepository.java
│   ├── ClubRepository.java
│   ├── TableRepository.java
│   ├── StaffRepository.java
│   ├── ShiftRepository.java
│   ├── PromotionRepository.java
│   ├── ProductRepository.java
│   ├── BillRepository.java
│   ├── BillItemRepository.java
│   └── AttendanceRepository.java
├── entity/
│   ├── User.java
│   ├── Club.java
│   ├── Table.java
│   ├── Staff.java
│   ├── Shift.java
│   ├── Promotion.java
│   ├── Product.java
│   ├── Bill.java
│   ├── BillItem.java
│   └── Attendance.java
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── GoogleAuthRequest.java
│   │   └── ...
│   ├── response/
│   │   ├── AuthResponse.java
│   │   ├── ApiResponse.java
│   │   └── ...
└── exception/
    ├── GlobalExceptionHandler.java
    ├── CustomException.java
    └── ...
```

## Required Dependencies (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Google OAuth -->
    <dependency>
        <groupId>com.google.api-client</groupId>
        <artifactId>google-api-client</artifactId>
        <version>1.35.2</version>
    </dependency>
</dependencies>
```

## Application Properties

```properties
# Server configuration
server.port=8080
server.servlet.context-path=/api

# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/billard_management
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT configuration
jwt.secret=your-jwt-secret-key
jwt.expiration=86400000
jwt.refresh-expiration=604800000

# Google OAuth
google.client-id=your-google-client-id
google.client-secret=your-google-client-secret

# CORS configuration
cors.allowed-origins=http://localhost:3000,http://localhost:5173
```

## Key Entity Examples

### User Entity
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String firstName;
    private String lastName;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    private UserRole role;
    
    private String avatar;
    private final boolean isActive = true;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // getters and setters
}

enum UserRole {
    ADMIN, CUSTOMER, STAFF
}
```

### Club Entity
```java
@Entity
@Table(name = "clubs")
public class Club {
    @Id
    private String id;
    
    private String name;
    private String address;
    private String phone;
    private String email;
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    
    private final boolean isActive = true;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // getters and setters
}
```

## API Endpoints Structure

### Authentication Endpoints
- POST `/api/auth/login` - User login
- POST `/api/auth/register` - User registration
- POST `/api/auth/logout` - User logout
- POST `/api/auth/refresh` - Refresh token
- POST `/api/auth/forgot-password` - Forgot password
- POST `/api/auth/reset-password` - Reset password
- GET `/api/auth/profile` - Get user profile
- PUT `/api/auth/profile` - Update user profile
- POST `/api/auth/google` - Google OAuth login

### Admin Endpoints
- GET `/api/admin/customers` - Get all customers
- GET `/api/admin/customers/{id}` - Get customer details
- PATCH `/api/admin/customers/{id}/status` - Update customer status
- DELETE `/api/admin/customers/{id}` - Delete customer
- GET `/api/admin/admins` - Get all admins
- POST `/api/admin/admins` - Create new admin
- PUT `/api/admin/admins/{id}` - Update admin
- DELETE `/api/admin/admins/{id}` - Delete admin
- GET `/api/admin/statistics` - Get system statistics

### Customer Endpoints
- GET `/api/customer/clubs` - Get user's clubs
- POST `/api/customer/clubs` - Create new club
- PUT `/api/customer/clubs/{id}` - Update club
- DELETE `/api/customer/clubs/{id}` - Delete club
- GET `/api/customer/tables` - Get tables
- POST `/api/customer/tables` - Create table
- PUT `/api/customer/tables/{id}` - Update table
- DELETE `/api/customer/tables/{id}` - Delete table
- (Similar patterns for staff, shifts, promotions, products)

### Staff Endpoints
- GET `/api/staff/bills` - Get bills
- POST `/api/staff/bills` - Create new bill
- PUT `/api/staff/bills/{id}` - Update bill
- PATCH `/api/staff/bills/{id}/complete` - Complete bill
- GET `/api/staff/schedule` - Get work schedule
- POST `/api/staff/attendance/check-in` - Check in
- PATCH `/api/staff/attendance/{id}/check-out` - Check out

## Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
                .requestMatchers("/api/staff/**").hasRole("STAFF")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

## Error Handling

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException ex) {
        return ResponseEntity.status(ex.getStatus())
            .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException ex) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("Validation failed", ex.getErrors()));
    }
}
```

## Environment Variables

Create a `.env` file or set system environment variables:

```
REACT_APP_API_BASE_URL=http://localhost:8080/api
DB_HOST=localhost
DB_PORT=3306
DB_NAME=billard_management
DB_USERNAME=your_username
DB_PASSWORD=your_password
JWT_SECRET=your-super-secret-jwt-key
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

## Database Schema

The system requires the following main tables:
- users
- clubs
- tables
- staff
- shifts
- promotions
- products
- bills
- bill_items
- attendance

## Getting Started

1. Create a new Spring Boot project with the required dependencies
2. Set up your database (MySQL recommended)
3. Configure the application properties
4. Implement the entities, repositories, services, and controllers
5. Set up security configuration with JWT
6. Test the API endpoints
7. Deploy both frontend and backend

The React frontend is already configured to work with this backend structure through the API service layer.