#!/usr/bin/env bash
set -e

echo "==============================================================================="
echo "LabRox LIMS Enterprise Bootstrap (Docker Environment)"
echo "==============================================================================="

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
INFRA_DIR="$ROOT_DIR/infra"
BACKEND_DIR="$ROOT_DIR/labrox-backend"

# 1. Build Maven packages if running locally
if command -v mvn &> /dev/null; then
    echo "[1/3] Building backend JARs..."
    cd "$BACKEND_DIR"
    mvn clean package -DskipTests
fi

# 2. Boot Docker Compose stack
echo "[2/3] Starting Docker Compose platform stack..."
cd "$INFRA_DIR"
docker compose down -v || true
docker compose up -d --build

# 3. Wait for gateway and healthcheck
echo "[3/3] Waiting for Gateway to be ready on http://localhost:8088..."
until curl -s http://localhost:8088/actuator/health | grep "UP" > /dev/null; do
    echo "Waiting for Gateway health check..."
    sleep 3
done

echo "==============================================================================="
echo "LabRox LIMS System Online!"
echo "Gateway:  http://localhost:8088"
echo "Frontend: http://localhost:3000"
echo "Default SuperAdmin: admin / Admin@123"
echo "==============================================================================="
