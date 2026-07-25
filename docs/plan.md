# Kế hoạch học tập và triển khai dự án E-commerce Microservices

Dự án này là một hệ thống thương mại điện tử hoàn chỉnh với kiến trúc Microservices, bao gồm 13 backend services (Spring Boot 3), API Gateway (Apache APISIX), Frontend (Next.js), và được triển khai trên Kubernetes.

Để học và clone lại dự án này một cách hiệu quả, chúng ta không nên code tất cả cùng một lúc. Thay vào đó, kế hoạch dưới đây sẽ chia nhỏ quá trình học và triển khai thành từng giai đoạn (Phases) từ cơ bản đến nâng cao.

# Quy tắc triển khai
- Xác định yêu cầu sẽ liên quan đến phase, đọc yêu cầu phase đó trong file docs/phase/phase_?.md tương ứng ở root path project 

## User Review Required

Không có câu hỏi mở nào thêm. Kế hoạch đã được cập nhật dựa trên yêu cầu: 
- Sử dụng đúng 100% stack công nghệ của dự án mẫu.
- Chạy môi trường Local bằng Docker Compose trong suốt quá trình dev core.
- Triển khai lên AWS khi hoàn thiện.

## Proposed Changes (Các Giai Đoạn Triển Khai)

Dưới đây là lộ trình từng bước để bạn tự tay build lại hệ thống này:

### Giai đoạn 1 (doc: Phase1.md): Khởi tạo Project & Common Libraries (Nền tảng)
- **Cấu trúc thư mục gốc**: Tạo repository mới và thiết lập `pom.xml` cha (Parent POM) để quản lý phiên bản dependencies cho toàn bộ project.
- **Xây dựng `common-lib`**: Thay vì lặp lại code ở 13 services, chúng ta sẽ viết các thư viện dùng chung:
  - `common-core`: Các class Exception chung, Response format chuẩn, Utils.
  - `common-security`: Cấu hình phân quyền JWT / OAuth2.
  - `common-logging`: Cơ chế log tập trung (AOP).
  - `common-kafka`: Các class hỗ trợ produce/consume message.

### Giai đoạn 2: Infrastructure & Security Setup
- **Cơ sở dữ liệu**: Khởi tạo PostgreSQL, Redis, Elasticsearch thông qua Docker Compose.
- **Identity Provider (IAM)**: Thiết lập Keycloak bằng Docker, cấu hình Realm, Client và Test cấp phát JWT token.
- **Message Broker**: Khởi tạo Kafka (KRaft mode).
- **Service Auth**: Xây dựng `auth-service` (nếu cần xử lý logic riêng biệt hoặc wrapper API cho Keycloak).

### Giai đoạn 3: Core Business Microservices
Đây là các service cốt lõi nhất để một hệ thống E-commerce hoạt động:
- **`product-service`**: Quản lý danh mục, sản phẩm.
- **`inventory-service`**: Quản lý kho, số lượng hàng hóa (Xử lý vấn đề Race condition, Distributed Lock với Redis).
- **`order-service`**: Quản lý đặt hàng. Xử lý bài toán Distributed Transaction (Saga Pattern hoặc 2PC) khi vừa phải tạo đơn hàng vừa phải trừ kho.
- **Giao tiếp giữa các service**: Tích hợp OpenFeign để gọi API đồng bộ, hoặc dùng Kafka cho bất đồng bộ.

### Giai đoạn 4: API Gateway & Hỗ trợ vận hành
- **Thiết lập API Gateway**: Cấu hình Apache APISIX (hoặc Spring Cloud Gateway). Route các request từ client vào đúng service (Product, Order...).
- **Tích hợp Rate Limiting & Auth**: Bắt Gateway phải validate token từ Keycloak trước khi cho request đi tiếp vào bên trong.

### Giai đoạn 5: Các dịch vụ bổ trợ & Xử lý bất đồng bộ
- **`media-service`**: Xử lý upload ảnh/video sử dụng RustFS (S3-compatible).
- **`notification-service`**: Lắng nghe event từ Kafka (ví dụ: có đơn hàng mới) để gửi Email/SMS thông báo cho người dùng.
- **`payment-service`**, **`shipping-service`**, **`tax-service`**: Các service mô phỏng thanh toán, tính thuế, giao hàng.

### Giai đoạn 6: Search & Tính năng nâng cao
- **`search-service`**: Đồng bộ dữ liệu từ PostgreSQL (Product) sang Elasticsearch thông qua Kafka/Debezium (CDC) để thực hiện Full-text Search tốc độ cao.
- **`rating-service`**, **`favourite-service`**, **`promotion-service`**: Hoàn thiện các tính năng cho người dùng.

### Giai đoạn 7: Frontend & Tích hợp (Local)
- **Frontend**: Xây dựng giao diện bằng Next.js tích hợp với API Gateway.
- **Local Testing**: Sử dụng Docker Compose để đóng gói (Jib/Dockerfile) và chạy toàn bộ hệ thống (13 services + Frontend + DB/Infra) để kiểm thử End-to-End.

### Giai đoạn 8: Triển khai lên AWS (Production)
- **Chuẩn bị hạ tầng AWS**: Cấu hình cơ sở dữ liệu và message broker trên cloud (VD: AWS RDS cho PostgreSQL, ElastiCache cho Redis, MSK cho Kafka).
- **Container Orchestration**: Sử dụng AWS ECS (Elastic Container Service) hoặc AWS EKS (Elastic Kubernetes Service) cho việc chạy các microservices.
- **Lưu trữ tĩnh**: Đổi RustFS thành Amazon S3 thực tế.
- **CI/CD**: Xây dựng GitHub Actions để tự động build image, push lên Amazon ECR và update môi trường production.

---

## Verification Plan

Sau mỗi giai đoạn, chúng ta sẽ thực hiện kiểm tra:

### Kiểm tra tự động (Automated Tests)
- Viết Unit Test & Integration Test (sử dụng Testcontainers) cho từng service.
- Chạy thử các build commands: `mvn clean package -DskipTests=false`

### Kiểm tra thủ công (Manual Verification)
- Khởi động service thông qua file `docker-compose.yml`.
- Sử dụng Postman hoặc Swagger UI (`http://localhost:<port>/swagger-ui.html`) để gọi và test API.
- Xem log trên console để phát hiện lỗi bảo mật, lỗi kết nối DB hoặc Kafka.

