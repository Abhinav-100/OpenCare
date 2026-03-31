#!/bin/bash

# Set environment
ENV=$1
DOCKER_USERNAME="imran110219"
CONTAINER_NAME="open-care-backend"

ENV_FILE="$(pwd)/.env.$ENV"
IMAGE_NAME="$DOCKER_USERNAME/open-care-backend:$ENV-latest"

# Validate input environment
if [[ "$ENV" != "dev" && "$ENV" != "qa" && "$ENV" != "prod" ]]; then
    echo "❌ Usage: ./deploy.sh [dev|qa|prod]"
    exit 1
fi

echo "🚀 Deploying '$ENV' environment..."

# Check if Docker is installed and running
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install it first."
    exit 1
fi

if ! systemctl is-active --quiet docker; then
    echo "❌ Docker is not running. Please start it with: sudo systemctl start docker"
    exit 1
fi

# Pull the latest image
echo "📥 Pulling latest image: $IMAGE_NAME..."
docker pull $IMAGE_NAME || { echo "❌ Failed to pull Docker image!"; exit 1; }

# Stop and remove old container (if running)
echo "🛑 Stopping existing container (if running)..."
docker stop $CONTAINER_NAME 2>/dev/null || true
docker rm $CONTAINER_NAME 2>/dev/null || true

# Clean up unused images to free space
echo "🧹 Cleaning up unused Docker resources..."
docker system prune -af

# Run the new container
echo "🚀 Starting new container..."
docker run -d --name $CONTAINER_NAME -p 127.0.0.1:6700:6700 --restart always \
  --env SPRING_PROFILES_ACTIVE=$ENV \
  --env-file $ENV_FILE \
  $IMAGE_NAME || { echo "❌ Failed to start the container!"; exit 1; }

echo "✅ Deployment to '$ENV' environment completed successfully!"
