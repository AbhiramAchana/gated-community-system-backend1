# Gated Community Management System - Backend

> Spring Boot REST API for managing gated community operations including property management, invoice generation, payment processing, visitor tracking, and facility booking.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)

## 🚀 Features

- **Authentication & Authorization**: JWT-based auth with role-based access control (Admin, Resident, Gate Security)
- **Property Management**: CRUD operations for properties with owner/tenant assignments
- **Invoice & Payments**: Automated billing with Razorpay payment gateway integration
- **Visitor Management**: Pre-approval system with check-in/check-out tracking
- **Complaint System**: Multi-status complaint tracking with admin responses
- **Facility Booking**: Community facility reservation with time slot management
- **Real-time Notifications**: WebSocket integration for live updates
- **Staff Management**: Employee records and attendance tracking
- **Email Notifications**: Automated emails for important events

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.x
- **Security**: Spring Security + JWT
- **Database**: PostgreSQL (Supabase)
- **ORM**: Hibernate/JPA
- **Payment**: Razorpay API
- **Real-time**: WebSocket (STOMP)
- **Documentation**: Swagger/OpenAPI
- **Build Tool**: Maven

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL database (or Supabase account)
- Razorpay account (test keys)
- Gmail account (for email notifications)

## ⚙️ Local Development Setup

### 1. Clone the Repository

```bash
git clone <your-backend-repo-url>
cd backend
```

### 2. Configure Environment Variables

Copy the example file:
```bash
cp .env.example .env.local
```

Edit `.env.local` with your credentials:
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/postgres
SPRING_DATASOURCE_USERNAME=your-username
SPRING_DATASOURCE_PASSWORD=your-password

SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password

RAZORPAY_KEY_ID=rzp_test_xxxxx
RAZORPAY_KEY_SECRET=your-secret

JWT_SECRET=your-256-bit-secret
FRONTEND_URL=http://localhost:5173
```

### 3. Run the Application

**Using Maven:**
```bash
./mvnw spring-boot:run
```

**Using IntelliJ IDEA:**
1. Open project in IntelliJ
2. Install EnvFile plugin
3. Edit Run Configuration → Add `.env.local`
4. Run `BackendApplication`

### 4. Verify Setup

- **API Health**: http://localhost:8080/api/test
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

## 🧪 Testing

Run unit and integration tests:
```bash
./mvnw test
```

## 📦 Build for Production

```bash
./mvnw clean package -DskipTests
```

The JAR file will be in `target/backend-0.0.1-SNAPSHOT.jar`

## 🌐 Deployment

### Railway (Recommended)

1. Connect your GitHub repo to Railway
2. Set environment variables in Railway dashboard
3. Railway auto-deploys on push to main branch

### Docker

```bash
docker build -t gated-community-backend .
docker run -p 8080:8080 --env-file .env.local gated-community-backend
```

## 📚 API Documentation

Once running, access interactive API docs at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### Key Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/auth/login` | POST | User login |
| `/api/auth/register` | POST | User registration |
| `/api/properties/admin/all` | GET | Get all properties |
| `/api/invoices/resident/{id}` | GET | Get resident invoices |
| `/api/payments/create-order` | POST | Create Razorpay order |
| `/api/visitors/gate/all` | GET | Get all visitors |
| `/api/complaints/resident/create` | POST | Submit complaint |

## 🔒 Security

- JWT tokens expire after 24 hours
- Passwords hashed with BCrypt
- CORS configured for frontend domain
- Role-based endpoint protection
- SQL injection prevention via JPA

⚠️ **Never commit:**
- `.env.local`
- `application-local.yml`
- Any file with real credentials

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is part of an academic capstone project.

## 👥 Authors

- Your Name - [(https://github.com/AbhiramAchana)]

## 🔗 Links

- **Frontend Repository**: [(https://github.com/AbhiramAchana/gated-community-system-frontend1)]
- **Live Demo**: [(https://gated-community-system-frontend1.vercel.app/)]

