# user-service

Spring Boot service quản lý hồ sơ người dùng và các role `CUSTOMER`, `PM`, `ADMIN`.
Service không chứa password, login, JWT hoặc Keycloak.

## Chạy local

Khởi động PostgreSQL từ root repository:

```powershell
docker compose up -d --wait postgres
```

Build:

```powershell
.\mvnw.cmd -B -ntp -pl user-service -am package -DskipTests
```

Chạy với local profile:

```powershell
java -jar user-service\target\user-service-0.1.0-SNAPSHOT.jar --spring.profiles.active=local
```

Service tự đọc `.env` ở root. Mặc định chỉ bind vào `127.0.0.1:8081`.

Kiểm tra:

```powershell
Invoke-RestMethod http://127.0.0.1:8081/actuator/health
```

## API

| Method | Endpoint |
| :--- | :--- |
| `POST` | `/api/v1/users` |
| `GET` | `/api/v1/users/{id}` |
| `GET` | `/api/v1/users?page=0&size=20&keyword=` |
| `PUT` | `/api/v1/users/{id}` |
| `PUT` | `/api/v1/users/{id}/roles` |

Endpoint gán role chỉ được bật bởi profile `local`. Không public service trước khi
có authentication và authorization.
