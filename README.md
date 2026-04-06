# Backend - Gated Community Management System

## Setup for Local Development

### 1. Configure Environment Variables

Copy the example environment file:
```bash
cp .env.example .env.local
```

Edit `.env.local` and fill in your actual credentials:
- Database credentials (Supabase)
- Email credentials (Gmail)
- Razorpay keys
- JWT secret

### 2. Run the Application

#### Option A: Using Maven with Environment Variables

**Windows (PowerShell):**
```powershell
# Load environment variables from .env.local
Get-Content .env.local | ForEach-Object {
    if ($_ -match '^([^=]+)=(.*)$') {
        [Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process')
    }
}

# Run the application
./mvnw spring-boot:run
```

**Linux/Mac (Bash):**
```bash
# Load environment variables and run
export $(cat .env.local | xargs) && ./mvnw spring-boot:run
```

#### Option B: Using IDE (IntelliJ IDEA / Eclipse)

1. Install EnvFile plugin (IntelliJ) or similar
2. Configure run configuration to load `.env.local`
3. Run the application

### 3. Verify Application is Running

Open browser and go to:
- API: http://localhost:8080/api/test
- Swagger UI: http://localhost:8080/swagger-ui.html

## Production Deployment

For production deployment, environment variables should be set in your hosting platform:
- Railway: Set in Variables tab
- AWS: Set in Elastic Beanstalk environment
- Docker: Pass via docker-compose.yml or -e flags

See `../DEPLOYMENT_GUIDE.md` for detailed instructions.

## Security Notes

⚠️ **NEVER commit the following files:**
- `.env.local` - Contains your actual credentials
- `application-local.yml` - If you create one with credentials

✅ **Safe to commit:**
- `.env.example` - Template without real values
- `application.yml` - Uses environment variables
- `application-prod.yml` - Uses environment variables

## Environment Variables Reference

See `.env.example` for all required environment variables.
