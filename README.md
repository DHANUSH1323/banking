# Banking Microservices Platform

A complete FinTech-style microservice architecture built with Spring Boot.

## Microservices
- **service-registry** - Eureka Service Discovery
- **api-gateway** - Gateway routing & load balancing
- **user-service** - Handles user registration & login
- **account-service** - Manages bank accounts
- **fund-transfer-service** - Handles money transfers
- **transaction-service** - Logs transactions
- **sequence-generator** - Generates unique IDs

## Stack
- Java 17
- Spring Boot 3.5.x
- Spring Cloud (Eureka, Gateway, Feign)
- Kafka
- Redis
- MySQL
- Docker

### Run Order
1. Start `service-registry`  
2. Start `api-gateway`  
3. Start other services (`user`, `account`, etc.)
