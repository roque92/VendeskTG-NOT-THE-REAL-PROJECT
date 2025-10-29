# VendeskGT

[![Java](https://img.shields.io/badge/Java-21-blue)](https://www.oracle.com/java/) 
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-green)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## Project Description
VendeskGT is a **multi-tenant SaaS platform** designed for small businesses, specifically school stores.  
It allows **merchants** to manage their inventory, product catalog, and sales, while **parents and teachers** can place orders for students from their trusted school store.

The goal of VendeskGT is to provide a seamless ordering system where:
- Teachers or parents can create orders for students.
- Merchants can manage products, inventory, and sales efficiently.

---

## Current Status
- 🔧 **In early development**
- Merchant registration API is implemented.
- Sensitive data (user and password) is stored in **Firebase Authentication**.
- General merchant data is stored in **PostgreSQL**.
- Email confirmation sent via **Resend**.
- Secrets managed via **Infisical**.

---

## Technology Stack
- **Language:** Java 21  
- **Framework:** Spring Boot 3.5.5  
- **Authentication:** Firebase Authentication (planned)  
- **Database:** PostgreSQL  
- **Email Service:** Resend  
- **Secrets Management:** Infisical  
- **Containerization:** Docker  
- **Serverless:** AWS SAM / Lambda (for testing)

---

### nstallation & Running Locally
To run VendeskGT locally:

```bash
./gradlew clean build && sam build && sam local start-api --env-vars "env.json"

```

---

### Endpoints (MVP)

Register a Merchant

- **POST** /tenants/register

Description:
Registers a new merchant in the system. Each merchant will have a dedicated sub-schema for their store.

**Request Body (JSON):**
```
{
  "name": "Juan",
  "lastName": "Fuentes",
  "schemaName": "Tienda JHire",
  "contactEmail": "vendestkgt-testing@mailinator.com",
  "password": "MyPass12345"
}
```


**Response (JSON):**
```
{
  "tenantId": "480585e8-fe6e-4cff-ae74-3f8346cfc087",
  "schemaName": "tiendajhire",
  "name": "Juan",
  "lastName": "Fuentes",
  "contactEmail": "vendestkgt-testing@mailinator.com",
  "planType": null,
  "paymentMethod": null,
  "subscriptionStartDate": null,
  "subscriptionEndDate": null
}
```
# Notes
```
- Passwords are stored in Firebase Authentication.
- General merchant data is stored in PostgreSQL.
- A verification email is sent via Resend.
- Secrets are handled via Infisical.
```

# ⚠️ Note
```
he code snippets and endpoints shown in this README are demonstrative only. 
Cloning this repository will not allow running them directly. 
They serve to showcase code structure and development capabilities. 
The full project is under active development and will be deployed on AWS.
```
---

### Roadmap / Next Steps

- Complete user registration and login with Firebase Authentication.
- Implement role management (merchant, client, admin).
- Develop a web frontend for end users.
- Deploy backend fully on AWS (SAM / Lambda).
- Version the API (v1) for future releases.

---

### Architecture

**VendeskGT** follows a tiered architecture:

- Controller Layer: Handles incoming requests.
- Service Layer: Business logic.
- Repository Layer: Database access.

Each merchant has a dedicated schema for multitenancy.

---

### Contributing
```
Currently, this is a personal project. 
Contributions are welcome in the future once the project reaches 
a stable release.
```
