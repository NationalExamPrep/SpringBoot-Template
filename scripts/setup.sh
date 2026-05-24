#!/bin/bash

echo "==================================="
echo "Spring Boot Starter Setup Script"
echo "==================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

echo "✅ Java version:"
java -version

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.6+."
    exit 1
fi

echo "✅ Maven version:"
mvn -version

# Check if PostgreSQL is running
if ! command -v psql &> /dev/null; then
    echo "⚠️  PostgreSQL client not found. Make sure PostgreSQL is installed and running."
else
    echo "✅ PostgreSQL client found"
fi

# Create .env file if it doesn't exist
if [ ! -f .env ]; then
    echo "📝 Creating .env file from .env.example..."
    cp .env.example .env
    echo "✅ .env file created. Please update it with your configuration."
else
    echo "✅ .env file already exists"
fi

# Create uploads directory
mkdir -p uploads
echo "✅ Uploads directory created"

# Build the project
echo "🔨 Building the project..."
mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo ""
    echo "==================================="
    echo "Setup Complete!"
    echo "==================================="
    echo ""
    echo "Next steps:"
    echo "1. Update .env file with your database credentials"
    echo "2. Start PostgreSQL database"
    echo "3. Run: mvn spring-boot:run"
    echo "4. Access Swagger UI: http://localhost:8080/swagger-ui.html"
    echo ""
    echo "Or use Docker Compose:"
    echo "  docker-compose up --build"
else
    echo "❌ Build failed. Please check the errors above."
    exit 1
fi
