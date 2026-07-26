# Phase 2 — Infrastructure & Security Setup

## Mục tiêu

- Xây dựng hạ tầng local ổn định bằng Docker Compose.
- Thiết lập PostgreSQL, Redis, Elasticsearch, Kafka KRaft và Keycloak.
- Chuẩn hóa cấu hình, health check, dữ liệu khởi tạo và bảo mật cho môi trường phát triển.

## Các bước đánh giá và triển khai

1. Đánh giá phiên bản, tài nguyên và cổng sử dụng của từng thành phần.
2. Thiết kế Docker Compose, network, volume, health check và biến môi trường.
3. Khởi tạo Keycloak realm, client, role và kiểm thử JWT.
4. Khởi tạo Kafka topic, retention, retry topic và dead-letter topic cơ bản.
5. Kiểm thử kết nối từ service mẫu tới từng thành phần hạ tầng.
6. Chỉ triển khai sau khi kế hoạch chi tiết được người dùng đồng ý.

## Phạm vi

- PostgreSQL
- Redis
- Elasticsearch
- Kafka ở chế độ KRaft
- Keycloak
- Docker Compose và cấu hình local
- `auth-service` nếu cần logic nghiệp vụ ngoài Keycloak

## Tiêu chí hoàn thành

- Các container khởi động ổn định và có health check.
- Dữ liệu được giữ qua lần restart bằng volume.
- Có thể cấp và xác thực JWT từ Keycloak.
- Producer và consumer mẫu trao đổi được Kafka event.
- Cấu hình bí mật không được commit trực tiếp vào repository.

## Nhật ký cải tiến

Ghi các cải tiến theo ngày tại thư mục `done/` với tên file `day-month.md`.
