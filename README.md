# 🚲 Bike Taxi App - Production Ready Backend

A high-performance, scalable Spring Boot backend for a real-time bike taxi application with JWT authentication, payment processing, real-time communication, and comprehensive admin features.

## 🚀 Features

- **🔐 JWT Authentication** with Clerk integration
- **💬 Real-time Communication** via Socket.IO with Netty
- **💳 Payment Processing** with Razorpay integration
- **📸 Image Upload & Processing** with validation
- **👥 Role-based Access Control** (RIDER, DRIVER, ADMIN)
- **🐳 Production-ready** with Docker & Railway deployment
- **📊 Comprehensive Error Handling** and logging
- **🏥 Health Monitoring** with Spring Actuator
- **⚡ Performance Optimized** with connection pooling
- **🔍 Admin Dashboard** with analytics and management
- **📱 Real-time Location Tracking** for drivers and rides

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Mobile App    │    │   Admin Panel   │
│   (React/Vue)   │    │   (React Native)│    │   (React)       │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────▼─────────────┐
                    │    Bike Taxi Backend      │
                    │   (Spring Boot 3.2.5)     │
                    └─────────────┬─────────────┘
                                  │
          ┌───────────────────────┼───────────────────────┐
          │                       │                       │
┌─────────▼─────────┐  ┌─────────▼─────────┐  ┌─────────▼─────────┐
│   PostgreSQL      │  │     Redis         │  │   Socket.IO       │
│   (Database)      │  │   (Cache/Queue)   │  │   (Real-time)     │
└───────────────────┘  └───────────────────┘  └───────────────────┘
```

## 🔧 Technology Stack

- **Framework**: Spring Boot 3.2.5
- **Java**: OpenJDK 17 (Eclipse Temurin)
- **Database**: PostgreSQL 15
- **Cache**: Redis 7
- **Authentication**: Clerk JWT
- **Payments**: Razorpay
- **Real-time**: Socket.IO with Netty
- **Containerization**: Docker & Docker Compose
- **Deployment**: Railway (Cloud Platform)
- **Monitoring**: Spring Actuator
- **Documentation**: Swagger/OpenAPI 3

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 15+
- Redis 7+
- Docker & Docker Compose (for production)
- Railway account (for cloud deployment)

## 🚀 Quick Start

### Development Mode

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd bike-app
   ```

2. **Set up database**
   ```sql
   CREATE DATABASE biketaxi;
   ```

3. **Start in development mode**
   ```bash
   # Windows
   start-app.bat
   
   # Linux/Mac
   chmod +x start-app.sh
   ./start-app.sh
   ```

4. **Access the application**
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui/
   - Socket.IO: http://localhost:9092
   - Health Check: http://localhost:8080/actuator/health

### Production Mode

1. **Set environment variables**
   ```bash
   # Database
   export DATABASE_URL="jdbc:postgresql://localhost:5432/biketaxi"
   export DATABASE_USERNAME="postgres"
   export DATABASE_PASSWORD="your_secure_password"
   
   # Razorpay
   export RAZORPAY_KEY_ID="your_production_key_id"
   export RAZORPAY_KEY_SECRET="your_production_secret"
   
   # Clerk
   export CLERK_PUBLISHABLE_KEY="your_production_publishable_key"
   export CLERK_SECRET_KEY="your_production_secret_key"
   export CLERK_JWT_ISSUER="your_production_issuer"
   export CLERK_WEBHOOK_SECRET="your_webhook_secret"
   
   # Redis
   export REDIS_HOST="localhost"
   export REDIS_PORT="6379"
   export REDIS_PASSWORD="your_redis_password"
   ```

2. **Start in production mode**
   ```bash
   # Windows
   start-app.bat
   
   # Linux/Mac
   chmod +x start-app.sh
   ./start-app.sh
   ```

## 🐳 Docker Deployment

### Using Docker Compose (Recommended)

1. **Create environment file**
   ```bash
   cp .env.example .env
   # Edit .env with your production values
   ```

2. **Start all services**
   ```bash
   docker-compose up -d
   ```

3. **Check status**
   ```bash
   docker-compose ps
   docker-compose logs -f app
   ```

### Manual Docker Build

1. **Build the image**
   ```bash
   docker build -t biketaxi-app .
   ```

2. **Run the container**
   ```bash
   docker run -d \
     --name biketaxi-app \
     -p 8080:8080 \
     -p 9092:9092 \
     -e SPRING_PROFILES_ACTIVE=prod \
     -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/biketaxi \
     biketaxi-app
   ```

## 🚂 Railway Deployment

### Deploy to Railway

1. **Connect your repository to Railway**
   - Go to Railway dashboard
   - Create new project
   - Connect your GitHub repository

2. **Set environment variables in Railway**
   ```bash
   SPRING_PROFILES_ACTIVE=prod
   DATABASE_URL=your_railway_postgres_url
   DATABASE_USERNAME=postgres
   DATABASE_PASSWORD=your_railway_password
   RAZORPAY_KEY_ID=your_razorpay_key
   RAZORPAY_KEY_SECRET=your_razorpay_secret
   CLERK_PUBLISHABLE_KEY=your_clerk_key
   CLERK_SECRET_KEY=your_clerk_secret
   CLERK_JWT_ISSUER=your_clerk_issuer
   ```

3. **Deploy**
   - Railway will automatically detect the Dockerfile
   - Build and deploy your application
   - Access via the provided Railway URL

## 🔐 JWT Authentication

### Frontend Integration

The backend expects JWT tokens from Clerk in the following format:

```javascript
// Frontend code example
const token = await clerk.session.getToken();

// Include in API requests
fetch('/api/users/me', {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});

// Socket.IO connection
const socket = io('http://localhost:9092', {
  query: { token: token }
});
```

### Token Structure

```json
{
  "sub": "user_2abc123def456",
  "iss": "https://your-clerk-instance.clerk.accounts.dev",
  "iat": 1640995200,
  "exp": 1640998800,
  "email": "user@example.com",
  "name": "John Doe"
}
```

### Backend Verification

The backend automatically:
1. Extracts tokens from `Authorization` header or query parameters
2. Verifies signature using Clerk's public key (RS256)
3. Validates issuer and expiration
4. Creates or retrieves user from database
5. Sets up Spring Security context

## 📡 API Endpoints

### Public Endpoints (No Authentication)

```
POST /api/registration/user     - Register new rider
POST /api/registration/driver   - Register new driver
POST /api/registration/vehicle  - Register vehicle
PUT  /api/registration/driver/{id}/verify    - Verify driver
PUT  /api/registration/vehicle/{id}/verify   - Verify vehicle
POST /api/payments/create-order - Create payment order
POST /api/payments/verify       - Verify payment
POST /api/payments/refund       - Process refund
GET  /api/payments/{id}         - Get payment details
GET  /api/payments/key          - Get Razorpay key ID
POST /api/images/upload         - Upload image
GET  /api/images/{id}           - Get image
POST /api/webhooks/razorpay     - Razorpay webhook
POST /api/clerk/webhook         - Clerk webhook
GET  /api/auth/config           - Get Clerk configuration
GET  /swagger-ui/**             - Swagger UI
GET  /v3/api-docs/**            - OpenAPI docs
GET  /actuator/health           - Health check
GET  /actuator/info             - Application info
```

### Protected Endpoints (JWT Required)

#### User Management (RIDER, DRIVER, ADMIN)
```
GET  /api/users/me             - Get current user profile
PUT  /api/users/me             - Update user profile
```

#### Driver Management (DRIVER, ADMIN)
```
GET  /api/drivers/me                  - Get driver profile
PUT  /api/drivers/me/status           - Update driver status
PUT  /api/drivers/me/location         - Update driver location
GET  /api/drivers/me/rides            - Get driver rides
```

#### Ride Management (RIDER, DRIVER, ADMIN)
```
POST /api/rides/request               - Request ride (RIDER)
PUT  /api/rides/{id}/accept           - Accept ride (DRIVER)
PUT  /api/rides/{id}/start            - Start ride (DRIVER)
PUT  /api/rides/{id}/complete         - Complete ride (DRIVER)
PUT  /api/rides/{id}/cancel           - Cancel ride (RIDER/DRIVER)
POST /api/rides/{id}/verify-otp       - Verify OTP (DRIVER)
GET  /api/rides/nearby                - Find nearby drivers
GET  /api/rides/{rideId}/locations    - Get ride locations
GET  /api/rides/estimate-fare         - Estimate fare
```

#### Rating & Invoice (RIDER, DRIVER, ADMIN)
```
POST /api/ratings                     - Submit rating
GET  /api/ratings/{id}                - Get rating
GET  /api/invoices/{id}               - Get invoice
```

#### Admin Functions (ADMIN ONLY)
```
GET  /api/admin/users                 - List all users
GET  /api/admin/rides                 - List all rides
GET  /api/admin/vehicles              - List all vehicles
GET  /api/admin/earnings              - Get earnings report
PUT  /api/admin/surge/update          - Update surge pricing
GET  /api/admin/drivers/online        - Get online drivers
GET  /actuator/**                     - All actuator endpoints
```

## 🔧 Configuration

### Profiles

- **dev**: Development mode with relaxed security
- **prod**: Production mode with strict security

### Key Configuration Files

- `application.properties` - Default configuration
- `application-dev.properties` - Development settings
- `application-prod.properties` - Production settings

### Environment Variables

| Variable | Description | Required | Default |
|----------|-------------|----------|---------|
| `DATABASE_URL` | PostgreSQL connection URL | Yes | - |
| `DATABASE_USERNAME` | Database username | Yes | postgres |
| `DATABASE_PASSWORD` | Database password | Yes | - |
| `RAZORPAY_KEY_ID` | Razorpay public key | Yes | - |
| `RAZORPAY_KEY_SECRET` | Razorpay secret key | Yes | - |
| `CLERK_PUBLISHABLE_KEY` | Clerk publishable key | Yes | - |
| `CLERK_SECRET_KEY` | Clerk secret key | Yes | - |
| `CLERK_JWT_ISSUER` | Clerk JWT issuer | Yes | - |
| `REDIS_HOST` | Redis host | No | localhost |
| `REDIS_PORT` | Redis port | No | 6379 |
| `REDIS_PASSWORD` | Redis password | No | - |
| `SPRING_PROFILES_ACTIVE` | Active profile | No | prod |

## 📊 Monitoring & Health Checks

### Actuator Endpoints

```
GET /actuator/health     - Application health
GET /actuator/info       - Application info
GET /actuator/metrics    - Application metrics
GET /actuator/prometheus - Prometheus metrics
```

### Health Check Response

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL"
      }
    },
    "redis": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 419430400000,
        "threshold": 10485760
      }
    }
  }
}
```

## 🔒 Security Features

### Production Security

- **JWT Authentication**: All protected endpoints require valid JWT
- **Role-based Access**: RIDER, DRIVER, ADMIN roles
- **CORS Protection**: Restricted to specific domains
- **Input Validation**: Comprehensive request validation
- **SQL Injection Protection**: JPA/Hibernate with parameterized queries
- **XSS Protection**: Input sanitization and output encoding
- **Rate Limiting**: Configurable rate limiting (can be added)

### Development Security

- **Relaxed Authentication**: All endpoints accessible for testing
- **CORS**: Allows all origins for development
- **Debug Logging**: Detailed security logs

## 🚀 Performance Optimizations

### JVM Settings

```bash
# Production (Railway)
-Xmx300m -Xms128m

# Development
-Xmx4g -Xms2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError
```

### Database Optimizations

- Connection pooling with HikariCP
- Prepared statements
- Index optimization
- Query optimization

### Image Processing

- Efficient byte array handling
- File size validation (10MB max)
- Format validation
- Memory-optimized processing

## 📝 Testing

### API Testing

```bash
# Test user registration
curl -X POST http://localhost:8080/api/registration/user \
  -F "clerkUserId=user_test123" \
  -F "firstName=John" \
  -F "lastName=Doe" \
  -F "email=john@example.com"

# Test with JWT token
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Socket.IO Testing

```javascript
// Connect to Socket.IO
const socket = io('http://localhost:9092', {
  query: { token: 'YOUR_JWT_TOKEN' }
});

// Send location update
socket.emit('location_update', {
  latitude: 12.9716,
  longitude: 77.5946
});
```

## 🐛 Troubleshooting

### Common Issues

1. **OutOfMemoryError**: Increase JVM heap size
2. **Database Connection**: Check PostgreSQL is running
3. **JWT Validation**: Verify Clerk configuration
4. **Socket.IO Connection**: Check authentication bypass settings
5. **Railway Deployment**: Check environment variables

### Logs

```bash
# View application logs
tail -f logs/bike-taxi-app.log

# Docker logs
docker-compose logs -f app

# Railway logs
railway logs
```

## 📈 Scaling

### Horizontal Scaling

1. **Load Balancer**: Use Nginx or HAProxy
2. **Database**: PostgreSQL read replicas
3. **Cache**: Redis cluster
4. **Application**: Multiple instances behind load balancer

### Vertical Scaling

1. **JVM Heap**: Increase `-Xmx` and `-Xms`
2. **Database**: Increase PostgreSQL resources
3. **Connection Pool**: Adjust HikariCP settings

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the documentation
- Review the logs for errors
- Check the security analysis in `SECURITY_ANALYSIS.md`

---

**Bike Taxi App** - Production Ready Backend 🚀

*Built with Spring Boot 3.2.5, Java 17, and modern cloud deployment practices* 