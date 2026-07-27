# Kế hoạch triển khai Phase 2A — Docker Compose và PostgreSQL

## Trạng thái

- Trạng thái: `COMPLETED — USER CONFIRMED`
- Phạm vi tối giản đã được người dùng xác nhận ngày 27/07/2026.
- PostgreSQL local chỉ dùng superuser mặc định; chưa tạo role, database hoặc schema nghiệp vụ.
- Cấu hình, Makefile, runbook và kiểm chứng kỹ thuật đã hoàn thành.
- Người dùng nghiệm thu kết quả lúc `01:12:13 - 27/07/2026`.

## Mục tiêu

Tạo hạ tầng local nhỏ nhất để tập trung phát triển tính năng:

- Docker Compose chỉ chạy PostgreSQL.
- PostgreSQL major version 16, có health check và dữ liệu bền vững.
- Chỉ dùng superuser `postgres` và database mặc định `postgres`.
- Có `.env.example`, `.env`, Makefile và runbook đơn giản.
- Không chạy hạ tầng chưa có consumer.

## Quyết định đơn giản hóa

- Dùng image `postgres:16-alpine`.
- `make up` và `make restart` pull image mới nhất trong nhánh PostgreSQL 16 trước khi chạy.
- Không tạo application role, migration role hoặc database riêng ở Phase 2A.
- Không tạo init script và không tạo business table.
- Chỉ bind PostgreSQL vào loopback của máy local.
- Dùng default network do Docker Compose tự quản lý.
- Khi bắt đầu service đầu tiên, cấu hình database-per-service sẽ được quyết định trong kế hoạch của service đó.

## Phạm vi

### Thực hiện

- Chuẩn hóa `docker-compose.yml` chỉ còn service `postgres`.
- Cấu hình credentials và port qua `.env`.
- Cung cấp `.env.example` có comment.
- Thêm named volume và health check `pg_isready`.
- Tạo Makefile cho các tác vụ phổ biến.
- Viết runbook vận hành local.
- Kiểm chứng SQL và persistence bằng bảng smoke test tạm thời.

### Không thực hiện

- Redis, Kafka, Keycloak hoặc Elasticsearch.
- `auth-service`, `product-service` hoặc business schema.
- Role/database PostgreSQL riêng cho từng service.
- pgAdmin hoặc công cụ GUI.
- Prometheus, Grafana, tracing backend.
- Kubernetes, CI/CD hoặc cấu hình production.

## Cấu trúc

```text
MicroREpo/
├── .env
├── .env.example
├── .gitignore
├── docker-compose.yml
├── Makefile
└── docs/
    └── phase/
        └── phase2/
            ├── phase2.md
            ├── basic-infrastructure-implementation-plan.md
            ├── postgres-local-runbook.md
            └── done/
                └── DD-MM.md
```

## Checklist triển khai

### Bước 1 — Docker Compose

- [x] Bỏ Redis khỏi Compose.
- [x] Dùng image `postgres:16-alpine`.
- [x] Không đặt `container_name`.
- [x] Bind port vào `127.0.0.1` và cho phép override port từ `.env`.
- [x] Thêm named volume.
- [x] Thêm health check với interval, timeout, retries và start period.

**Hoàn thành khi:** `docker compose config --quiet` hợp lệ và chỉ liệt kê service `postgres`.

### Bước 2 — Cấu hình local

- [x] Điền `.env.example` kèm comment.
- [x] Tạo `.env` local dùng `postgres` superuser.
- [x] Xác nhận `.env` bị Git ignore.
- [x] Không đưa credential production vào repository.

**Hoàn thành khi:** Compose nhận đủ biến và `.env` không xuất hiện trong Git.

### Bước 3 — Makefile

- [x] `make up`: pull image PostgreSQL 16 rồi khởi động và chờ healthy.
- [x] `make down`: dừng và xóa container/network của project, giữ volume.
- [x] `make restart`: down, pull image và chạy lại.
- [x] `make clear CONFIRM=YES`: xóa container, network, image và volume.
- [x] Thêm target hỗ trợ `config`, `ps`, `logs`, `health`, `verify`.
- [x] Đánh dấu các target bằng `.PHONY`.

**Hoàn thành khi:** các target không phá dữ liệu hoạt động; `clear` từ chối chạy nếu thiếu `CONFIRM=YES`.

### Bước 4 — Runbook

- [x] Hướng dẫn chuẩn bị `.env`.
- [x] Hướng dẫn start, stop, restart, status và log.
- [x] Hướng dẫn health check và `SELECT 1`.
- [x] Hướng dẫn đổi port khi `5432` bị chiếm.
- [x] Cảnh báo rõ `make clear CONFIRM=YES` xóa toàn bộ dữ liệu PostgreSQL local.

**Hoàn thành khi:** developer có thể vận hành PostgreSQL chỉ bằng tài liệu.

### Bước 5 — Kiểm chứng

- [x] Chạy `docker compose config --quiet`.
- [x] Xác nhận Compose chỉ có service `postgres`.
- [x] Chạy `make up`.
- [x] Xác nhận container `healthy`.
- [x] Chạy SQL bằng superuser `postgres`.
- [x] Tạo bảng/dữ liệu smoke test tạm thời.
- [x] Restart và xác nhận dữ liệu tồn tại.
- [x] Down/up không kèm `-v` và xác nhận dữ liệu vẫn tồn tại.
- [x] Xóa bảng smoke test.
- [x] Xác nhận port chỉ bind vào `127.0.0.1`.
- [x] Xác nhận named volume và default network được tạo.
- [x] Xác nhận `.env` không được Git theo dõi.
- [x] Ghi kết quả vào nhật ký ngày thực hiện.

**Hoàn thành khi:** toàn bộ kiểm chứng pass mà không chạy thêm thành phần hạ tầng nào.

## Thứ tự kích hoạt sau Phase 2A

1. Xây dựng service đầu tiên và quyết định database/schema của service đó.
2. Thêm Keycloak khi bắt đầu đăng nhập hoặc phân quyền.
3. Thêm Redis khi có use case cache/lock cụ thể.
4. Thêm Kafka khi có producer, consumer và event contract.
5. Thêm Elasticsearch cùng `search-service` ở Phase 6.

## Rủi ro

- `make up` cần mạng khi pull image; image đã có vẫn có thể dùng trực tiếp bằng `docker compose up -d --wait postgres`.
- Port `5432` có thể bị chiếm; đổi `POSTGRES_PORT` trong `.env`.
- `make clear CONFIRM=YES` xóa named volume và toàn bộ dữ liệu local.
- Credential trong `.env` chỉ dành cho local, không tái sử dụng ở production.
- Dùng superuser giúp đơn giản Phase 2A nhưng phải được xem xét lại trước môi trường shared hoặc production.

## Definition of Done

- [x] Người dùng duyệt kế hoạch Phase 2A và phạm vi superuser-only.
- [x] Checklist Bước 1–5 hoàn thành.
- [x] PostgreSQL local hoạt động và giữ dữ liệu qua restart/down-up.
- [x] Tài liệu vận hành và nhật ký kiểm chứng đầy đủ.
- [x] Người dùng nghiệm thu kết quả triển khai lúc `01:12:13 - 27/07/2026`.
