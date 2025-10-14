# Billiards Club Management System - Development Setup Guide

## Prerequisites

### Required Software
1. **Node.js** (v18 or later) - [Download here](https://nodejs.org/)
2. **Java JDK** (v17 or later) - [Download here](https://adoptium.net/)
3. **VS Code** - [Download here](https://code.visualstudio.com/)
4. **Git** - [Download here](https://git-scm.com/)

### Recommended VS Code Extensions
Install these extensions for the best development experience:

**For React Frontend:**
- ES7+ React/Redux/React-Native snippets
- TypeScript Importer
- Tailwind CSS IntelliSense
- Auto Rename Tag
- Bracket Pair Colorizer
- Prettier - Code formatter
- ESLint

**For Spring Boot Backend:**
- Extension Pack for Java
- Spring Boot Extension Pack
- REST Client (for API testing)

## Project Structure Overview

```
billiard-management/
├── frontend/                 # React TypeScript Frontend
│   ├── src/
│   ├── package.json
│   └── ...
└── backend/                  # Spring Boot Backend
    ├── src/main/java/
    ├── pom.xml
    └── ...
```

## Frontend Setup (React + TypeScript)

### 1. Clone and Navigate to Project
```bash
# If cloning from repository
git clone <your-repository-url>
cd billiard-management/frontend

# Or if you already have the project
cd path/to/your/frontend-project
```

### 2. Install Dependencies
```bash
# Install Node.js dependencies
npm install

# Or using yarn
yarn install
```

### 3. Environment Configuration
Create a `.env` file in the frontend root:

```env
# API Configuration
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_NAME=Billiard Management System

# Development settings
VITE_NODE_ENV=development
```

### 4. Package.json Scripts
Ensure your `package.json` has these scripts:

```json
{
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext ts,tsx --report-unused-disable-directives --max-warnings 0",
    "type-check": "tsc --noEmit"
  }
}
```

### 5. Running the Frontend
```bash
# Start development server
npm run dev

# The app will be available at http://localhost:5173
```

## Backend Setup (Spring Boot)

### 1. Create Spring Boot Project Structure
```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── billiard/
│   │   │           └── management/
│   │   │               ├── BilliardManagementApplication.java
│   │   │               ├── config/
│   │   │               ├── controller/
│   │   │               ├── entity/
│   │   │               ├── repository/
│   │   │               ├── service/
│   │   │               └── dto/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── static/
│   └── test/
├── pom.xml
└── README.md
```

### 2. Essential Dependencies (pom.xml)
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
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt</artifactId>
        <version>0.9.1</version>
    </dependency>
    
    <!-- Development -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### 3. Application Configuration (application.yml)
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/billiard_management
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
  
  security:
    jwt:
      secret: your-secret-key
      expiration: 86400000 # 24 hours

# CORS Configuration
cors:
  allowed-origins: http://localhost:5173
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: "*"
```

### 4. Running the Backend
```bash
# Navigate to backend directory
cd backend

# Run using Maven
./mvnw spring-boot:run

# Or using Gradle
./gradlew bootRun

# The API will be available at http://localhost:8080
```

## VS Code Workspace Configuration

### 1. Create Workspace File
Create a `billiard-management.code-workspace` file:

```json
{
  "folders": [
    {
      "name": "Frontend",
      "path": "./frontend"
    },
    {
      "name": "Backend", 
      "path": "./backend"
    }
  ],
  "settings": {
    "typescript.preferences.includePackageJsonAutoImports": "auto",
    "editor.formatOnSave": true,
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  },
  "extensions": {
    "recommendations": [
      "bradlc.vscode-tailwindcss",
      "esbenp.prettier-vscode",
      "ms-vscode.vscode-typescript-next",
      "vscjava.vscode-java-pack"
    ]
  }
}
```

### 2. Open Workspace in VS Code
```bash
code billiard-management.code-workspace
```

## Development Workflow

### 1. Start Both Services
```bash
# Terminal 1 - Backend
cd backend
./mvnw spring-boot:run

# Terminal 2 - Frontend  
cd frontend
npm run dev
```

### 2. Access Applications
- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html (if Swagger is configured)

### 3. Testing API Endpoints
Create a `test-api.http` file in VS Code (requires REST Client extension):

```http
### Test Authentication
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}

### Test Protected Endpoint
GET http://localhost:8080/api/customers
Authorization: Bearer {{token}}
```

## Common Development Tasks

### Frontend Development
```bash
# Add new dependencies
npm install <package-name>

# Type checking
npm run type-check

# Build for production
npm run build
```

### Backend Development
```bash
# Add new dependencies (in pom.xml)
./mvnw dependency:resolve

# Run tests
./mvnw test

# Package application
./mvnw package
```

## Troubleshooting

### Common Issues

1. **CORS Errors**
   - Ensure backend CORS configuration allows frontend origin
   - Check that API URLs in frontend match backend

2. **Port Conflicts**
   - Frontend default: 5173 (Vite)
   - Backend default: 8080
   - Change ports in configuration if needed

3. **Database Connection**
   - Ensure MySQL is running
   - Verify database credentials in application.yml

4. **JWT Token Issues**
   - Check token expiration
   - Verify JWT secret configuration

### VS Code Tips

1. **Multi-root Workspace**: Use the workspace file to work on both frontend and backend simultaneously
2. **Integrated Terminal**: Use Ctrl+` to open terminal panels for both projects
3. **Extensions**: Install language-specific extensions for better IntelliSense
4. **Debug Configuration**: Set up launch configurations for both Node.js and Java debugging

## Next Steps

1. Set up your database schema
2. Configure authentication endpoints
3. Implement role-based access control
4. Add API documentation with Swagger
5. Set up automated testing
6. Configure CI/CD pipeline

This setup gives you a complete development environment for your Billiards Club Management System with hot reloading, proper TypeScript support, and seamless integration between frontend and backend.