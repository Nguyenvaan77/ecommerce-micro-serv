# Phase 1

## Mục tiêu:
- Trước mắt xây dựng `common-core` cho các service Java/Spring Boot.

## Trạng thái

- Phase 1 đã chốt phạm vi ở `common-core`; không triển khai toàn bộ common library khi chưa có consumer thực tế.
- `common-core` đã được triển khai, kiểm chứng 28/28 test và có trong commit `1f9bc53`.
- Kế hoạch triển khai chi tiết: [common-library-implementation-plan.md](common-library-implementation-plan.md).
- Danh sách 7 module bên dưới là roadmap tham khảo. Các module còn lại chỉ triển khai theo nhu cầu của service ở phase sau.
- Nhật ký được ghi ngay tại [done/25-07.md](done/25-07.md); từng mục được đánh dấu `ĐÃ XÁC NHẬN` hoặc `CHƯA XÁC NHẬN`.

## Các bước đánh giá và triển khai:
- Bước 1: Đánh giá xem project hiện tại nên triển khai library gì. Ghi vào file README.md
- Bước 2: Đưa ra quan điểm vì sao lại cần -> giải quyết cái gì -> service nào sẽ cần đến library đấy -> đưa ra hướng triển khai
- Bước 3: Tạo common library sau khi có sự đồng ý của tôi
- Bước 4: Lưu lại những thay đổi bằng commit log nhỏ trong chính file Phase.md này

---

## 🏗️ Kế Hoạch Đánh Giá & Triển Khai Chi Tiết Common Libraries (Bước 1 & 2)

Hệ sinh thái `common-lib` được chia thành **7 module quan trọng**, tuân theo kiến trúc DRY (Don't Repeat Yourself) và chuẩn hóa microservices:

### 1. `common-core` (Hạt nhân ứng dụng & Chuẩn hóa giao tiếp)
- **Vì sao cần:** Tránh hiện tượng "mỗi developer một kiểu trả về" (nơi trả về `{"error": "..."}`, nơi trả về `{"message": "..."}`) gây xung đột dữ liệu khi Frontend hoặc service khác tích hợp.
- **Giải quyết cái gì:**
  - Chuẩn hóa cấu trúc **API Response & Global Exception Handling** (`@ControllerAdvice`, `@ExceptionHandler`).
  - Định nghĩa mã lỗi chuẩn (`ErrorCode`, `Constants`, `ApiPaths`).
  - Tích hợp **i18n (Đa ngôn ngữ)** trong thông báo lỗi.
- **Service sử dụng:** **100% (Tất cả 13 services).**
- **Hướng triển khai:** Định nghĩa các lớp exception gốc (`BusinessException`, `NotFoundException`,...), các DTO bọc phản hồi và cấu hình đọc properties lỗi vi/en.

### 2. `common-security` (Bảo mật & Phân quyền tập trung)
- **Vì sao cần:** Phải lặp lại 13 lần cấu hình Spring Security để validate JWT Token từ Keycloak gửi về là thảm họa khi bảo trì hoặc khi có nâng cấp bảo mật.
- **Giải quyết cái gì:**
  - Định nghĩa `SecurityFilterChain` gốc (Stateless OAuth2 Resource Server).
  - Phân luồng Endpoint nào Public (như Actuator, Swagger), Endpoint nào cần bảo vệ.
  - Cung cấp tiện ích `AuthenticationUtils` để lấy `userId`, `roles` từ `SecurityContext` trong 1 dòng lệnh.
- **Service sử dụng:** **100% các Backend Services (ngoại trừ hạ tầng như APISIX Gateway).**
- **Hướng triển khai:** Tích hợp Spring Security & OAuth2 Resource Server (JOSE), expose các properties cho từng service tự định nghĩa đường dẫn public (`ecommerce.security.public-paths`).

### 3. `common-logging` (Giám sát & Đo lường hiệu năng tự động)
- **Vì sao cần:** Hạn chế việc code rác `log.info(...)` rải rác đầu/cuối mỗi hàm gây bẩn logic kinh doanh (Business Logic).
- **Giải quyết cái gì:**
  - Tự động ghi lại Request/Response HTTP thông qua **Spring AOP**.
  - **Đo lường hiệu năng:** Cảnh báo (`WARN`) trong log nếu có bất kỳ API/hàm nào thực thi vượt quá ngưỡng thời gian cấu hình (VD: > 50ms) để dễ dàng dò tìm nghẽn cổ chai (bottleneck).
- **Service sử dụng:** **100% các Backend Services.**
- **Hướng triển khai:** Viết Aspect can thiệp vào tầng Controller, Service và các annotation đánh dấu chuyên dụng.

### 4. `common-spring` (Cấu hình Spring & HTTP Client tối ưu)
- **Vì sao cần:** Giao tiếp REST nội bộ giữa các service (VD: Rating gọi Order) cần có HTTP Client hiệu năng cao, tránh tràn connect do mở kết nối không kiểm soát.
- **Giải quyết cái gì:**
  - Tự động cấu hình (Auto-configuration) bean `RestClient.Builder` bọc trên nền **Apache HttpClient 5**.
  - Quản lý Connection Pool, cấu hình Time-out (Connect timeout, Read timeout) để tránh treo hệ thống khi service đích chậm hoặc sập (Cascading failure).
- **Service sử dụng:** **Các service có gọi REST nội bộ** (`rating`, `shipping`, `tax`, `search`, `favourite`, `order`, `payment`, `promotion`).
- **Hướng triển khai:** Xây dựng `@AutoConfiguration` cho RestClient kết hợp với cấu hình properties từ `RestClientProperties`.

### 5. `common-kafka` (Giao tiếp bất đồng bộ & Event Driven / CDC)
- **Vì sao cần:** Tránh viết đi viết lại cấu hình serializer/deserializer, kafka template, hay logic consume event ở từng dịch vụ.
- **Giải quyết cái gì:**
  - Tiêu chuẩn hóa các model Event, bọc (wrap) `KafkaTemplate` để dễ dàng bắn event (Produce).
  - Hỗ trợ xử lý mẫu Debezium CDC (Change Data Capture) khi đồng bộ dữ liệu (VD: Postgres sang Elasticsearch).
- **Service sử dụng:** **Các service hoạt động theo Event-Driven:** `order`, `payment`, `notification`, `search`.
- **Hướng triển khai:** Cấu hình base cho Kafka Producer/Consumer qua Spring Kafka starter.

### 6. `common-keycloak` (Cầu nối quản trị IAM)
- **Vì sao cần:** Khi cần thao tác administrative với Keycloak (như query thông tin user, quản lý account, cập nhật state), việc gọi raw REST API dễ gây rò rỉ token hoặc rắc rối trong khâu xử lý Exception.
- **Giải quyết cái gì:**
  - Bọc các lời gọi REST API của Keycloak Admin vào trong class tiện ích (`KeycloakAuthClient`).
  - Dịch chuyển các HTTP error từ Keycloak sang `BusinessException` nội bộ một cách mượt mà.
- **Service sử dụng:** **`auth-service`** và các dịch vụ tương tác trực tiếp tới thông tin IAM của Khách hàng.
- **Hướng triển khai:** Kết hợp `RestClient` của `common-spring` với Keycloak REST URL properties.

### 7. `common-storage` (Trừu tượng hóa lưu trữ S3/RustFS)
- **Vì sao cần:** Giai đoạn local dùng **RustFS**, giai đoạn AWS dùng **Amazon S3**. Nếu bind cứng một thư viện duy nhất vào code thì sau này đụng đến tầng Storage khi lên AWS sẽ phải đập đi viết lại.
- **Giải quyết cái gì:**
  - Tạo Interface lưu trữ (Object Storage Abstraction) dùng **AWS SDK v2** (tương thích cả S3 lẫn RustFS/MinIO).
  - Khi triển khai AWS (Phase 8), chỉ cần đổi cấu hình Environment (Endpoint URL), **không cần sửa một dòng code business nào**.
- **Service sử dụng:** **`media-service`** (hoặc các service cần tải lên/xuống tài nguyên tĩnh).
- **Hướng triển khai:** Viết interface `ObjectStorageService` với hàm upload, download, delete, generate presigned url.

---

## Nhật ký thay đổi

Nhật ký chi tiết theo ngày: [done/25-07.md](done/25-07.md).

| Tiêu đề | Type | Description | Thời gian thực hiện | Xác nhận |
| :--- | :--- | :--- | :--- | :--- |
| Plan chi tiết 7 common modules | `PLANNING` / `DOCS` | Thêm kế hoạch chi tiết cho 7 module theo Bước 1 và Bước 2; roadmap này chưa đồng nghĩa với triển khai toàn bộ. | `02:45:46 PM - 25/07/2026` | `CHƯA XÁC NHẬN` |
| Kế hoạch triển khai `common-core` | `PLANNING` | Thu gọn phạm vi trước mắt còn common core Java. | Trước `04:19:00 PM - 25/07/2026` | `ĐÃ XÁC NHẬN` |
| Triển khai `common-core` | `CODE` / `CONFIG` / `TEST` / `DOCS` | Hoàn thiện mã nguồn, auto-configuration, i18n, Maven Wrapper, README và 28 test. | `04:25:49 PM - 25/07/2026` | `ĐÃ XÁC NHẬN` — 27/07/2026 |
