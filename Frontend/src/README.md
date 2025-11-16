# Billiards Club Management System

A comprehensive React + Spring Boot application for managing billiards clubs with role-based access control.

## 🚀 Quick Start

### Prerequisites
- Node.js (v18+)
- Java JDK (v17+)
- MySQL Database

### Frontend Setup
```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Access at http://localhost:5173
```

### Backend Setup
See `SPRING_BOOT_BACKEND_GUIDE.md` for detailed backend setup instructions.

## 🏗️ Project Structure

```
├── App.tsx                 # Main application component
├── components/            
│   ├── auth/              # Authentication components
│   ├── admin/             # Admin role components
│   ├── customer/          # Customer role components  
│   ├── staff/             # Staff role components
│   ├── dashboards/        # Role-specific dashboards
│   ├── layout/            # Layout components
│   └── ui/                # Reusable UI components
├── services/              # API service layers
├── types/                 # TypeScript interfaces
├── hooks/                 # Custom React hooks
├── config/                # Configuration files
└── utils/                 # Utility functions
```

## 👥 User Roles

### Admin
- Customer management
- Admin account creation
- System oversight

### Customer (Club Owner)
- Club management
- Table management
- Staff management
- Shift scheduling
- Promotion management
- Product management

### Staff
- Bill management
- Table operations
- Work schedule viewing
- Attendance tracking

## 🛠️ Development

### Available Scripts
- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint
- `npm run type-check` - Run TypeScript checking

### Tech Stack
- **Frontend**: React 18, TypeScript, Tailwind CSS v4
- **Backend**: Spring Boot, MySQL, JWT Authentication
- **UI Components**: Custom components with Shadcn/ui
- **State Management**: React Context API
- **API Communication**: Axios with custom hooks

## 📝 Development Guide

See `DEVELOPMENT_SETUP_GUIDE.md` for comprehensive setup instructions.

## 🎨 Design System

- **Colors**: Green primary (#16a34a), white, light gray
- **Typography**: Inter/Roboto fonts
- **Components**: Consistent UI components across all roles
- **Responsive**: Mobile-first design approach

## 🔐 Authentication

- JWT-based authentication
- Role-based access control
- Secure API endpoints
- Password reset functionality
- Profile management

## 📱 Features

### Core Features
- User authentication and authorization
- Role-based navigation
- Responsive design
- Real-time updates
- Form validation

### Admin Features
- Customer account management
- Admin account creation
- System monitoring

### Customer Features  
- Club profile management
- Table management
- Staff account creation
- Shift scheduling
- Promotion campaigns
- Product inventory

### Staff Features
- Bill processing
- Table status management
- Schedule viewing
- Attendance tracking

## 🚀 Deployment

### Frontend
```bash
npm run build
# Deploy dist/ folder to your hosting service
```

### Backend
```bash
./mvnw package
# Deploy the generated JAR file
```

## 📄 License

This project is licensed under the MIT License.