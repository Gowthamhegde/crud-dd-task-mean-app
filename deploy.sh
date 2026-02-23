#!/bin/bash

# Script to deploy on VM
# This script should be placed on your VM

echo "Pulling latest images from Docker Hub..."
docker-compose pull

echo "Stopping existing containers..."
docker-compose down

echo "Starting containers with new images..."
docker-compose up -d

echo "Cleaning up old images..."
docker image prune -f

echo "Deployment completed successfully!"
docker-compose ps
