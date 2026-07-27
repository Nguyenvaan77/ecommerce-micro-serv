# Kế hoạch triển khai `user-service` — Phase 3A

## Trạng thái

- Trạng thái kế hoạch: `APPROVED — ĐÃ XÁC NHẬN`.
- Quyết định triển khai `user-service` trước `auth-service`: `ĐÃ XÁC NHẬN`.
- Trạng thái implementation: `COMPLETED — USER CONFIRMED`.
- Người dùng yêu cầu bắt đầu triển khai lúc `14:59 - 27/07/2026`.
- Người dùng nghiệm thu kết quả lúc `16:02:28 - 27/07/2026`.
- Chưa tạo Git commit cho `user-service`.

## Mục tiêu

Xây dựng service Java/Spring Boot đầu tiên để quản lý hồ sơ người dùng và business role:

- Tạo, xem, liệt kê và cập nhật hồ sơ người dùng.
- Bảo đảm `username`, `email` và `phone` không trùng.
- Hỗ trợ ba role cố định: `CUSTOMER`, `PM`, `ADMIN`.
- Dùng `common-core` cho response, error và exception.
- Dùng PostgreSQL local đã có ở Phase 2A.
- Giữ ranh giới rõ ràng để tích hợp Keycloak hoặc `auth-service` về sau mà không phải viết lại domain người dùng.

## Quyết định kiến trúc

### Ranh giới service

| Thành phần | Sở hữu |
| :--- | :--- |
| `user-service` | Hồ sơ người dùng, định danh nội bộ `userId`, dữ liệu liên hệ và business role. |
| Keycloak — triển khai sau | Password, MFA, đăng nhập, token, session, email verification và identity provider. |
| `auth-service` — xem xét gần cuối dự án | Chỉ tạo nếu cần orchestration giữa Keycloak và `user-service`, compensation hoặc reconciliation; không tạo wrapper Keycloak rỗng. |

Quy tắc:

- Package gốc là `com.ecommerce.userservice`, không dùng `com.ecommerce.authservice`.
- Service khác chỉ lưu `userId` hoặc snapshot cần thiết; không đọc trực tiếp schema của `user-service`.
- `keycloakUserId` là mapping kỹ thuật nullable/unique, không phải primary key của domain.
- API chưa có authentication chỉ được dùng trong môi trường local/dev và không được public ra môi trường dùng chung.

### Role

- Giả định `PM` có nghĩa là `Product Manager`.
- Dùng entity `Role` với enum tên role gồm `CUSTOMER`, `PM`, `ADMIN`.
- Dùng quan hệ `@ManyToMany`, bảng `roles` và join table `user_role`.
- Mọi user tạo qua API đều mặc định có role `CUSTOMER`.
- `CreateUserRequest` và `UpdateUserRequest` không nhận `roles`.
- Cung cấp endpoint gán role để thuận tiện cho smoke test local; endpoint này phải được bảo vệ hoặc loại bỏ trước khi public service.
- Trong thiết kế hiện tại, `user-service` là source of truth của business role; khi tích hợp Keycloak, role được đồng bộ một chiều sang token claim.

### Điều chỉnh entity được đề xuất

Entity người dùng được chuyển từ `com.ecommerce.authservice.entity.User` sang `com.ecommerce.userservice.entity.User`.

| Thuộc tính | Quyết định MVP |
| :--- | :--- |
| `id` | Giữ `Long`, PostgreSQL sinh bằng identity. |
| `fullName` | Bắt buộc, trim, từ 3 đến 100 ký tự. |
| `username` | Bắt buộc, trim và lowercase trước khi lưu, từ 3 đến 100 ký tự, unique, chưa cho đổi ở MVP. |
| `email` | Bắt buộc, trim và lowercase, email hợp lệ, tối đa 254 ký tự, unique, chưa cho đổi ở MVP. Bỏ `@NaturalId`. |
| `gender` | Đổi từ `String` sang enum `MALE`, `FEMALE`, `OTHER`, `UNSPECIFIED`. |
| `phone` | Không bắt buộc. Nhận số di động Việt Nam dạng `0xxxxxxxxx` hoặc `+84xxxxxxxxx`, chuẩn hóa thành `+84xxxxxxxxx` trước khi lưu, unique. Blank được đổi thành `null`. |
| `avatar` | Không bắt buộc. Chỉ chấp nhận URI HTTP/HTTPS hợp lệ; backend không tải nội dung từ URL này. |
| `keycloakUserId` | Giữ nullable/unique theo model đã cung cấp nhưng không nhận từ request và không trả trong public response. |
| `roles` | `Set<Role>` qua join table `user_role`; bảng `roles` được seed ba giá trị cố định; mặc định `CUSTOMER`. |

Các validation của dữ liệu đầu vào đặt trên request DTO. Entity và Liquibase vẫn giữ constraint persistence tương ứng.

Regex phone hiện tại không được giữ vì mâu thuẫn với `@Size(max = 11)`: chuỗi bắt đầu bằng `+84` dài ít nhất 12 ký tự. MVP coi `phone` là số di động Việt Nam; nếu cần hỗ trợ số bàn hoặc quốc gia khác sẽ thiết kế riêng sau.

Không thêm `status`, audit timestamp, soft delete hoặc optimistic locking khi chưa có use case.

## Phạm vi MVP

### Thực hiện

- Module Maven `user-service` nằm trực tiếp ở root, cùng cấp với `docker-compose.yml`.
- REST API version `v1`.
- Entity, repository, service, DTO và mapper thủ công.
- Liquibase migration cho schema và bảng.
- Validation, normalization và unique constraint.
- Business error riêng của `user-service`.
- Actuator health endpoint.
- Unit test, MockMvc test và integration test bằng PostgreSQL Testcontainers.
- Tài liệu chạy local và API smoke test.

### Chưa thực hiện

- Password, login, logout, refresh token hoặc forgot password.
- Spring Security, JWT, Keycloak và `common-security`.
- Provisioning/synchronization với Keycloak.
- Role management dùng trong môi trường shared/production; milestone này chỉ có endpoint phục vụ local test.
- Xóa hoặc vô hiệu hóa user.
- Kafka event, Redis, OpenFeign hoặc service-to-service call.
- Thêm common library mới.
- Đóng gói `user-service` vào Docker Compose ở milestone đầu.

## API MVP

| Method | Endpoint | Mục đích |
| :--- | :--- | :--- |
| `POST` | `/api/v1/users` | Tạo hồ sơ, luôn gán `CUSTOMER`, trả `201 Created`. |
| `GET` | `/api/v1/users/{id}` | Lấy hồ sơ theo `userId`. |
| `GET` | `/api/v1/users?page=0&size=20&keyword=` | Liệt kê có phân trang; keyword tìm theo tên, username hoặc email. |
| `PUT` | `/api/v1/users/{id}` | Chỉ cập nhật `fullName`, `gender`, `phone`, `avatar`. |
| `PUT` | `/api/v1/users/{id}/roles` | Gán một hoặc nhiều role phục vụ kiểm thử local. |

DTO dự kiến:

- `CreateUserRequest`: `fullName`, `username`, `email`, `gender`, `phone`, `avatar`.
- `UpdateUserRequest`: `fullName`, `gender`, `phone`, `avatar`.
- `AssignRolesRequest`: tập role không rỗng, chỉ nhận `CUSTOMER`, `PM`, `ADMIN`.
- `UserResponse`: `id`, `fullName`, `username`, `email`, `gender`, `phone`, `avatar`, `roles`.

Không dùng JPA entity trực tiếp làm request/response để tránh mass assignment vào `roles` và `keycloakUserId`.

Response thành công dùng:

- `ApiResponse<UserResponse>`.
- `ApiResponse<PageResponse<UserResponse>>`.

Nếu page vượt quá phạm vi hợp lệ, service trả lỗi `400` thay vì để `PageResponse` phát sinh exception ngoài dự kiến.

## Persistence

Phase local tiếp tục giữ cấu hình tối giản của Phase 2A:

- PostgreSQL 16 chạy bằng Docker Compose.
- Dùng superuser `postgres` và database mặc định `postgres`.
- Liquibase tạo schema riêng `user_service`.
- Bảng nghiệp vụ:
  - `user_service.users`
  - `user_service.roles`
  - `user_service.user_role`
- Bảng changelog của Liquibase giữ ở schema `public`.
- `spring.jpa.hibernate.ddl-auto=validate`.
- `spring.jpa.open-in-view=false`.
- Service chạy từ Maven/IDE trên port mặc định `8081`; có thể override bằng environment variable.
- JDBC URL mặc định dùng port `5432`; máy hiện tại override sang `55432` theo `.env`.

Unique constraint có tên rõ ràng cho `username`, `email`, `phone` và `keycloak_user_id`. Kiểm tra tồn tại trước khi lưu chỉ nhằm trả lỗi thân thiện; database constraint vẫn là nguồn bảo đảm cuối cùng khi có request đồng thời.

## Error contract

Tạo `UserErrorCode implements ErrorCode` trong chính `user-service`:

| Error code | HTTP status |
| :--- | :--- |
| `USER_NOT_FOUND` | `404 Not Found` |
| `USERNAME_ALREADY_EXISTS` | `409 Conflict` |
| `EMAIL_ALREADY_EXISTS` | `409 Conflict` |
| `PHONE_ALREADY_EXISTS` | `409 Conflict` |
| `USER_PAGE_OUT_OF_RANGE` | `400 Bad Request` |

- Dùng `NotFoundException` và `BusinessException` từ `common-core`.
- Dùng `GlobalExceptionHandler`, `ApiResponse`, `ApiError` và `PageResponse` hiện có.
- Thêm `messages.properties` và `messages_vi.properties` riêng cho user error.
- Dịch lỗi unique constraint từ PostgreSQL thành `409`, không trả tên constraint hoặc chi tiết SQL cho client.
- Không sửa `common-core` nếu contract hiện tại đã đủ.

## Cấu trúc dự kiến

```text
user-service/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/ecommerce/userservice/
    │   │   ├── UserServiceApplication.java
    │   │   ├── controller/
    │   │   ├── dto/
    │   │   ├── entity/
    │   │   ├── error/
    │   │   ├── mapper/
    │   │   ├── repository/
    │   │   ├── service/
    │   │   └── validation/
    │   └── resources/
    │       ├── application.yml
    │       ├── messages.properties
    │       ├── messages_vi.properties
    │       └── db/changelog/
    └── test/
        └── java/com/ecommerce/userservice/
```

Giữ cấu trúc layered đơn giản; chưa cần tách nhiều module hoặc áp dụng CQRS.

## Dependency

### Runtime

- `common-core`
- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-actuator`
- `liquibase-core`
- PostgreSQL JDBC driver
- Lombok

### Test

<!-- - `spring-boot-starter-test`
- Testcontainers JUnit Jupiter
- Testcontainers PostgreSQL -->

- chưa yêu cầu làm test, cứ chạy, khởi động được là được

Chưa thêm Spring Security, Keycloak, Kafka, Redis, OpenFeign, MapStruct hoặc common library mới.

## Kế hoạch triển khai

### Bước 1 — Chốt contract và model

- [x] Xác nhận phạm vi API MVP.
- [x] Xác nhận `PM = Product Manager`.
- [x] Xác nhận tập giá trị `Gender`.
- [x] Xác nhận quy tắc phone Việt Nam và role source of truth.
- [x] Viết request/response contract trước implementation.

**Hoàn thành khi:** model và API không còn quyết định mở ảnh hưởng schema.

### Bước 2 — Khởi tạo module

- [x] Tạo `user-service` trực tiếp ở root.
- [x] Thêm module vào root `pom.xml`.
- [x] Cấu hình Java 21, Spring Boot 3.3.5 và dependency cần thiết.
- [x] Tạo `UserServiceApplication`.
- [x] Cấu hình datasource và port bằng environment variable.

**Hoàn thành khi:** application context khởi động và root Maven reactor nhận module.

### Bước 3 — Database và persistence

- [x] Tạo Liquibase master changelog.
- [x] Tạo schema `user_service`, bảng `users`, `roles` và `user_role`.
- [x] Tạo named unique constraints và index cần thiết.
- [x] Tạo `User`, `Role`, `Gender` và repository.
- [x] Bật Hibernate schema validation và tắt Open Session in View.

**Hoàn thành khi:** Liquibase migrate sạch và JPA validate schema thành công trên PostgreSQL.

### Bước 4 — Business logic

- [x] Tạo DTO và mapper thủ công.
- [x] Tạo normalizer cho username, email và phone.
- [x] Tạo user với role mặc định `CUSTOMER`.
- [x] Cài đặt get, list và update profile.
- [x] Cài đặt gán role phục vụ local test.
- [x] Chặn client sửa `username`, `email`, `roles` và `keycloakUserId`.
- [x] Chuyển unique violation thành business error `409`.

**Hoàn thành khi:** toàn bộ invariant được kiểm soát ở application layer và database.

### Bước 5 — REST API và common-core

- [x] Tạo controller cho năm endpoint MVP/local test.
- [x] Bọc success response bằng `ApiResponse`.
- [x] Dùng `PageResponse` cho phân trang.
- [x] Tạo `UserErrorCode` và message tiếng Anh/Việt.
- [x] Kiểm tra error handler không làm lộ thông tin nội bộ.

**Hoàn thành khi:** API trả đúng status và contract success/error thống nhất.

### Bước 6 — Test (Không triển khai test bây giờ)
<!-- 
- [ ] Unit test normalizer, mapper và service.
- [ ] Test role mặc định `CUSTOMER`.
- [ ] Test client không thể tự gán `PM`/`ADMIN`.
- [ ] Test phone ở dạng `0...`, `+84...`, blank và sai định dạng.
- [ ] MockMvc test create, get, list, patch, validation và error contract.
- [ ] Testcontainers PostgreSQL test Liquibase, repository và unique constraints.
- [ ] Test duplicate request/race condition trả `409`.
- [ ] Test page ngoài phạm vi trả `400`.

**Hoàn thành khi:** test không phụ thuộc H2 và toàn bộ Maven reactor pass. -->

### Bước 7 — Kiểm chứng local và tài liệu

- [x] Chạy PostgreSQL bằng Phase 2A.
- [x] Chạy `user-service` trên host.
- [x] Kiểm tra `/actuator/health`.
- [ ] Smoke test năm endpoint — không thực hiện theo yêu cầu chưa cần test ở milestone này.
- [x] Xác nhận schema chỉ do `user-service` quản lý.
- [x] Chạy `.\mvnw.cmd -B -ntp clean verify`.
- [x] Viết runbook/API examples và cập nhật nhật ký Phase 3.

**Hoàn thành khi:** service hoạt động với PostgreSQL local và có bằng chứng kiểm chứng trong nhật ký.

## Definition of Done

- [x] Kế hoạch được người dùng xác nhận trước khi triển khai.
- [x] `user-service` build độc lập và trong root Maven reactor.
- [x] Liquibase quản lý đầy đủ schema; Hibernate chỉ `validate`.
- [ ] Năm endpoint MVP/local test đúng contract.
- [x] User mới luôn có `CUSTOMER`; role đặc quyền chỉ được gán qua endpoint local đã chỉ rõ.
- [x] Username, email và phone được chuẩn hóa và bảo vệ bằng unique constraint.
- [x] Không có password, token hoặc logic Keycloak trong `user-service`.
- [x] Không thêm common library hoặc hạ tầng chưa dùng.
<!-- - [ ] Unit, MockMvc và Testcontainers test pass. -->
- [x] Nhật ký thay đổi ghi rõ mục nào đã/chưa được người dùng xác nhận.

## Rủi ro và cách kiểm soát

| Rủi ro | Kiểm soát |
| :--- | :--- |
| API chưa có auth làm lộ PII | Chỉ chạy local/dev; không public trước khi có security. |
| Client tự gán role đặc quyền | Create/update profile DTO không chứa roles; endpoint gán role chỉ dùng local và phải được bảo vệ trước khi public. |
| Trùng dữ liệu khi request đồng thời | Named unique constraint và dịch DB violation thành `409`. |
| Phone/email khác format nhưng cùng giá trị | Normalize trước khi lưu và test đầy đủ. |
| Role bị quản lý ở cả Keycloak và database | Chỉ định một source of truth và đồng bộ một chiều khi tích hợp auth. |
| `keycloakUserId` bị mass assignment | Không expose trong request/response public. |

## Các giả định đã được người dùng xác nhận

Kế hoạch đang dùng các giả định sau:

1. `PM` là `Product Manager`.
2. `Gender` gồm `MALE`, `FEMALE`, `OTHER`, `UNSPECIFIED`.
3. `phone` là số di động Việt Nam, optional và lưu chuẩn `+84xxxxxxxxx`.
4. `user-service` giữ source of truth của business role; Keycloak chỉ mirror role về sau.

Kế hoạch và kết quả implementation đã được người dùng xác nhận ngày 27/07/2026.
API smoke test và automated test vẫn được ghi đúng là chưa thực hiện; xác nhận của
người dùng không thay thế bằng chứng kiểm thử kỹ thuật này.
