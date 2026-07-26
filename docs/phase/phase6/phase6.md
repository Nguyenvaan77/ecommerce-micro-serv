# Phase 6 — Search & Advanced Features

## Mục tiêu

- Cung cấp full-text search qua Elasticsearch.
- Đồng bộ dữ liệu tìm kiếm bằng Kafka và Debezium CDC.
- Hoàn thiện rating, favourite và promotion theo bounded context riêng.

## Các bước đánh giá và triển khai

1. Thiết kế search index, mapping, analyzer và query contract.
2. Thiết kế pipeline CDC từ PostgreSQL tới Elasticsearch.
3. Triển khai rebuild index và phục hồi khi consumer bị gián đoạn.
4. Triển khai `rating-service`, `favourite-service` và `promotion-service`.
5. Đánh giá consistency, cache, authorization và hiệu năng truy vấn.
6. Bổ sung test dữ liệu lớn, replay event và migration index.
7. Chỉ triển khai từng phần sau khi kế hoạch chi tiết được người dùng đồng ý.

## Phạm vi

- `search-service`
- Debezium CDC
- Elasticsearch
- `rating-service`
- `favourite-service`
- `promotion-service`

## Tiêu chí hoàn thành

- Search index có thể rebuild từ nguồn dữ liệu đáng tin cậy.
- CDC event được xử lý idempotent và theo dõi được độ trễ.
- Các service nâng cao không chia sẻ database.
- Có kiểm thử relevance, hiệu năng và eventual consistency.

## Nhật ký cải tiến

Ghi các cải tiến theo ngày tại thư mục `done/` với tên file `day-month.md`.
