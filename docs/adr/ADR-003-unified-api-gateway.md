# ADR-003: Spring Cloud Gateway as Single-Port Unified Entry (Port 8088)

## Status
Accepted

## Context
Deploying 10+ microservices on separate ports requires complex cross-origin resource sharing (CORS) configurations, SSL termination overhead, and complicated client environment configurations.

## Decision
Route all frontend traffic exclusively through `labrox-gateway-service` on port `8088`. The gateway acts as a reverse proxy, handling CORS headers globally, terminating TLS, rate-limiting malicious traffic, and forwarding requests to downstream services based on URL route path matching.

## Consequences
- The frontend `.env` points cleanly to `http://localhost:8088`.
- No cross-origin preflight complexity across individual services.
- Microservice internal ports remain protected inside the internal network bridge.
