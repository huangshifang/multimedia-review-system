#!/bin/bash
# Build Docker images and export for offline deployment
set -e

cd "$(dirname "$0")/.."

echo "=== Step 1: Build backend JAR ==="
cd backend
mvn clean package -DskipTests -B
cd ..

echo ""
echo "=== Step 2: Build frontend ==="
cd frontend
npm ci
npm run build
cd ..

echo ""
echo "=== Step 3: Build Docker images ==="
docker build -t multimedia-review-backend:1.0.0 -f docker/backend/Dockerfile .
docker build -t multimedia-review-frontend:1.0.0 -f docker/frontend/Dockerfile .

echo ""
echo "=== Step 4: Export images for offline deployment ==="
mkdir -p docker/offline-package
docker save -o docker/offline-package/multimedia-review-backend.tar multimedia-review-backend:1.0.0
docker save -o docker/offline-package/multimedia-review-frontend.tar multimedia-review-frontend:1.0.0

# Copy deployment files
cp docker/docker-compose.yml docker/offline-package/
cp docker/deploy.sh docker/offline-package/

echo ""
echo "=== Done ==="
echo "Offline package ready at: docker/offline-package/"
echo "Copy the entire 'offline-package' folder to the target machine and run:"
echo "  cd offline-package && bash deploy.sh"
