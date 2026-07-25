Đây là bản techstack cũng mong muốn áp dụng cho dự án này, là mục tiêu cuối cùng của dự án.

Nhưng chúng ta sẽ phát triển theo các phase tích hợp từ đơn giản (MVP) đến phức tạp (để học).

|                   |                                                    |
|-------------------|----------------------------------------------------|
| ⚡ **Backend**     | 13 microservices · Java 21 · Spring Boot 3.3.5     |
| 🚪 **Gateway**    | Apache APISIX 3.9 · Rate limiting · JWT validation |
| 🗄️ **Databases** | PostgreSQL 16 · Redis 7 · Elasticsearch 8          |
| 📨 **Messaging**  | Apache Kafka 3.9 (KRaft mode, no Zookeeper)        |
| 🔐 **Auth**       | Keycloak 26 · OAuth2 / OIDC · JWT                  |
| ☁️ **Storage**    | RustFS (S3-compatible)                             |
| 🐳 **Deploy**     | Docker Compose · k3d / Kubernetes · ArgoCD         |

---

## 🛠️ Technology Stack

### Backend

|                   |                                                                  |
|-------------------|------------------------------------------------------------------|
| **Runtime**       | Java 21 (Virtual Threads)                                        |
| **Framework**     | Spring Boot 3.3.5, Spring Security 6, Spring Data JPA            |
| **Database**      | PostgreSQL 16, Liquibase migrations                              |
| **Search**        | Elasticsearch 8, Spring Data Elasticsearch                       |
| **Messaging**     | Apache Kafka 3.9 (KRaft), Spring Kafka                           |
| **Security**      | Keycloak 26, OAuth2 / OIDC, JWT, Spring Security Resource Server |
| **Gateway**       | Apache APISIX 3.9 (OpenID Connect, rate limiting, CORS)          |
| **Storage**       | RustFS (S3-compatible), AWS SDK v2                               |
| **Observability** | Micrometer, Prometheus, Spring Boot Actuator                     |
| **API Docs**      | Springdoc OpenAPI 3, Swagger UI w/ PKCE                          |
| **Resilience**    | Resilience4j (circuit breaker, retry)                            |
| **Build**         | Maven, Jib (Dockerless containerization)                         |

### Frontend

|               |                              |
|---------------|------------------------------|
| **Framework** | Next.js 16.2, React 19.2     |
| **State**     | Zustand 5, TanStack Query 5  |
| **Styling**   | Tailwind CSS 4, Lucide icons |
| **HTTP**      | Axios                        |

### Infrastructure

|                |                                               |
|----------------|-----------------------------------------------|
| **Local K8s**  | k3d (K3s in Docker), NGINX Ingress Controller |
| **Production** | Kubernetes, ArgoCD (GitOps)                   |
| **Registry**   | GitHub Container Registry (ghcr.io)           |
| **CI**         | GitHub Actions, SonarCloud                    |

---
