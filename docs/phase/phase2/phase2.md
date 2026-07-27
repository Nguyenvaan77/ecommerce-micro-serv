# Phase 2 — Hạ tầng local tối thiểu

## Trạng thái

- Chiến lược triển khai theo nhu cầu: `ĐÃ XÁC NHẬN`.
- Milestone hiện tại: `Phase 2A — Docker Compose + PostgreSQL`.
- Kế hoạch chi tiết và phạm vi superuser-only: `ĐÃ XÁC NHẬN`.
- Trạng thái triển khai: `COMPLETED — USER CONFIRMED`.
- Thời gian nghiệm thu: `01:12:13 - 27/07/2026`.

## Nguyên tắc

- Không cài toàn bộ tech stack chỉ để “chuẩn bị trước”.
- Mỗi thành phần phải có consumer đầu tiên và acceptance test cụ thể.
- Chỉ một milestone hạ tầng được active tại một thời điểm.
- Dùng Docker Compose cho local; chưa đưa Kubernetes, Prometheus hoặc công cụ quản trị GUI vào giai đoạn này.
- Mỗi service sở hữu database của mình; không dùng chung schema giữa các service.

## Phạm vi thực hiện trước — Phase 2A

- Chuẩn hóa `docker-compose.yml`.
- Chạy duy nhất PostgreSQL 16.x.
- Thêm cấu hình môi trường mẫu, volume và health check.
- Chỉ khởi tạo superuser `postgres` và database mặc định `postgres`.
- Chưa tạo role, database, init script hoặc schema nghiệp vụ riêng.
- Thêm Makefile cho up, down, restart, clear và kiểm chứng.
- Viết runbook vận hành và kiểm chứng dữ liệu tồn tại sau restart.

Kế hoạch thực thi: [basic-infrastructure-implementation-plan.md](basic-infrastructure-implementation-plan.md).

Runbook: [postgres-local-runbook.md](postgres-local-runbook.md).

## Kết quả kiểm chứng

- PostgreSQL thực tế: `16.14`.
- Container: `healthy`.
- Kết nối host: `127.0.0.1:55432` do port `5432` đang được PostgreSQL khác sử dụng.
- User/database: `postgres` / `postgres`.
- Named volume: `ecommerce-micro_postgres-data`.
- Default network: `ecommerce-micro_default`.
- Dữ liệu tồn tại qua `make restart` và `make down`/`make up`.
- Bảng smoke test đã được xóa sau khi kiểm chứng.

## Thành phần hoãn

| Thành phần | Điều kiện kích hoạt |
| :--- | :--- |
| Keycloak | Có endpoint cần bảo vệ và ít nhất một Spring Resource Server cần xác thực JWT. |
| Redis | `inventory-service` có use case cache TTL hoặc distributed lock cụ thể. |
| Kafka KRaft | Có event contract cùng ít nhất một producer và một consumer. |
| Elasticsearch | Bắt đầu `search-service` ở Phase 6. |
| `auth-service` | Có logic nghiệp vụ mà Keycloak không đáp ứng; không tạo wrapper rỗng. |
| Prometheus/Grafana/tracing backend | Có service chạy thực tế và cần thu thập metrics hoặc trace tập trung. |

## Tiêu chí hoàn thành Phase 2A

- `docker compose config` hợp lệ.
- Chỉ PostgreSQL được khởi động cho milestone hiện tại.
- PostgreSQL chuyển sang trạng thái `healthy`.
- Kết nối và chạy được `SELECT 1` bằng superuser `postgres`.
- Dữ liệu còn nguyên sau `restart` và chu kỳ `down`/`up` không kèm `-v`.
- `.env` không được Git theo dõi; `.env.example` không chứa secret thật.
- Có hướng dẫn start, stop, log, health, connect và reset dữ liệu.
- Không có Redis, Kafka, Keycloak hoặc Elasticsearch chạy khi chưa đạt activation gate.

## Nhật ký

- [25-07.md](done/25-07.md): khởi tạo cấu trúc tài liệu.
- [27-07.md](done/27-07.md): chốt chiến lược tối thiểu và lập kế hoạch Phase 2A.
