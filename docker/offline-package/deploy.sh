#!/bin/bash
# Offline deployment: load Docker images and start services
set -e

cd "$(dirname "$0")"

echo "=== Step 1: Load Docker images ==="
docker load -i multimedia-review-backend.tar
docker load -i multimedia-review-frontend.tar

echo ""
echo "=== Step 2: Start services ==="
docker compose up -d

echo ""
echo "=== Done ==="
echo "Frontend: http://localhost:${FRONTEND_PORT:-80}"
echo ""
echo "Useful commands:"
echo "  docker compose logs -f          # View logs"
echo "  docker compose restart          # Restart services"
echo "  docker compose down             # Stop services"
echo "  docker compose down -v          # Stop and remove all data"
