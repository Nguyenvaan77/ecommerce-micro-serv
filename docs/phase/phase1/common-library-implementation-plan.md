# Kế hoạch triển khai `common-core` Java — Phase 1

## Trạng thái

- Trạng thái: `COMPLETED — SCOPE CLOSED AT common-core`
- Mã nguồn và test đã hoàn thành.
- Người dùng đã chốt Phase 1 tại `common-core`; các common library khác được hoãn đến khi có consumer thực tế.
- Đã ghi nhật ký tại [done/25-07.md](done/25-07.md) và [done/27-07.md](done/27-07.md).

## Mục tiêu

Tạo một Java library dùng chung cho các Spring Boot service, tập trung vào:

- Chuẩn hóa API response.
- Chuẩn hóa error response.
- Exception dùng chung.
- Global exception handling.
- Validation error.
- Message i18n cơ bản.

Golang và `notification-service` chưa nằm trong phạm vi hiện tại.

## Cấu trúc dự kiến

```text
MicroREpo/
├── pom.xml
└── common-lib/
    └── common-core/
        ├── pom.xml
        └── src/
            ├── main/
            │   ├── java/
            │   └── resources/
            └── test/
                └── java/
```

- `pom.xml` ở root: parent/aggregator, quản lý Java và dependency version.
- `common-core`: một JAR để các Spring Boot service khai báo dependency.

Chưa tạo BOM hoặc tách nhiều Spring Boot starter ở bước này. Chỉ tách thêm module khi thực sự cần.

## Nội dung `common-core`

### API model

- [x] `ApiResponse<T>` cho response thành công.
- [x] `ApiError` cho response lỗi.
- [x] `FieldError` cho lỗi validation theo từng field.
- [x] `PageResponse<T>` cho dữ liệu phân trang.

### Error và exception

- [x] `ErrorCode` interface.
- [x] Các error code kỹ thuật cơ bản:
  - `VALIDATION_ERROR`
  - `BAD_REQUEST`
  - `RESOURCE_NOT_FOUND`
  - `UNAUTHORIZED`
  - `FORBIDDEN`
  - `INTERNAL_SERVER_ERROR`
- [x] `BusinessException`.
- [x] `NotFoundException`.
- [x] Không đặt error code nghiệp vụ của Product, Order hoặc Payment vào common.

### Spring Web

- [x] `GlobalExceptionHandler` dùng `@RestControllerAdvice`.
- [x] Mapping exception sang HTTP status và `ApiError`.
- [x] Xử lý `MethodArgumentNotValidException`.
- [x] Xử lý exception không xác định nhưng không trả stack trace cho client.

### i18n

- [x] `common-messages.properties`.
- [x] `common-messages_vi.properties`.
- [x] Fallback message khi không tìm thấy message key.
- [x] Cho phép service bổ sung message riêng.

## Thứ tự triển khai

### Bước 1 — Khởi tạo Maven

- [x] Chốt Java 21, Spring Boot 3.3.5 và `groupId` `com.ecommerce`.
- [x] Tạo root `pom.xml`.
- [x] Tạo module `common-lib/common-core`.
- [x] Cấu hình Maven Wrapper 3.9.9.

**Hoàn thành khi:** chạy được `mvn clean verify`.

### Bước 2 — Tạo API model và exception

- [x] Tạo `ApiResponse<T>`, `ApiError`, `FieldError`, `PageResponse<T>`.
- [x] Tạo `ErrorCode` và nhóm error code kỹ thuật.
- [x] Tạo `BusinessException` và `NotFoundException`.

**Hoàn thành khi:** model serialize đúng JSON và exception giữ đúng error code/message key.

### Bước 3 — Global exception handling

- [x] Tạo `GlobalExceptionHandler`.
- [x] Map business exception, validation exception và unknown exception.
- [x] Không trả thông tin nhạy cảm hoặc stack trace cho client.

**Hoàn thành khi:** test controller trả đúng HTTP status và cấu trúc lỗi.

### Bước 4 — i18n

- [x] Thêm message tiếng Anh và tiếng Việt.
- [x] Xử lý locale từ request.
- [x] Kiểm tra fallback locale và fallback message.

**Hoàn thành khi:** cùng một error code trả đúng message theo locale.

### Bước 5 — Test và nghiệm thu

- [x] Unit test cho model, error code và exception.
- [x] Test JSON serialization.
- [x] MockMvc test cho global exception handler.
- [x] Test validation và i18n.
- [x] Chạy `.\mvnw.cmd -B -ntp clean verify`.

**Hoàn thành khi:** toàn bộ test pass và JAR được tạo thành công.

## Quy tắc triển khai

- Không thêm class `Utils` hoặc `Constants` chung khi chưa có use case cụ thể.
- Không đưa entity, repository hoặc business logic vào `common-core`.
- Không phụ thuộc service cụ thể.
- Không hard-code URL, topic Kafka hoặc role nghiệp vụ.
- Public class phải có mục đích sử dụng rõ ràng và có test.
- Chưa cần publish artifact; giai đoạn đầu sử dụng Maven reactor hoặc `mvn install`.

## Definition of Done

- [x] Root Maven project và `common-core` build thành công.
- [x] API success/error response có cấu trúc nhất quán.
- [x] Validation error trả đúng danh sách field lỗi.
- [x] Business exception được map đúng HTTP status.
- [x] Unknown exception không làm lộ chi tiết nội bộ.
- [x] Message hoạt động với tiếng Anh, tiếng Việt và fallback.
- [x] Toàn bộ 28 test pass.
- [x] Ghi nhật ký thay đổi ngay khi thực hiện và đánh dấu trạng thái xác nhận.
- [x] Người dùng đồng ý dừng Phase 1 sau `common-core` và chuyển sang Phase 2.
- [x] Cập nhật các mục triển khai trong nhật ký thành `ĐÃ XÁC NHẬN`.

## Backlog sau `common-core`

Các nội dung sau chưa triển khai và chỉ được kích hoạt khi có consumer thực tế:

- `common-security`
- `common-logging` hoặc observability
- `common-spring`/HTTP client
- `common-kafka`
- `common-keycloak`
- `common-storage`
- Contract dùng chung Java–Go
- `notification-service` bằng Go/Gin
- Maven registry, CI/CD và release automation
