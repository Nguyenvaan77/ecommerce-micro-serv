# Phase 5 — Supporting Services & Async Processing

## Mục tiêu

- Xây dựng các service hỗ trợ thanh toán, giao hàng, thuế, media và thông báo.
- Dùng event-driven architecture cho các tác vụ lâu, bất đồng bộ hoặc dễ thất bại.
- Cho phép service sử dụng công nghệ phù hợp với bounded context.

## Các bước đánh giá và triển khai

1. Xác định boundary, dữ liệu và contract của từng service.
2. Chuẩn hóa event envelope, schema, retry, DLQ và idempotency.
3. Triển khai `media-service` với storage abstraction tương thích S3.
4. Triển khai `notification-service` bằng Go/Gin nếu được phê duyệt.
5. Triển khai `payment-service`, `shipping-service` và `tax-service`.
6. Kiểm thử partial failure, duplicate event và quy trình bù trừ.
7. Chỉ triển khai từng service sau khi kế hoạch chi tiết được người dùng đồng ý.

## Phạm vi

- `media-service`
- `notification-service`
- `payment-service`
- `shipping-service`
- `tax-service`
- Kafka event contracts, retry và DLQ
- Email/SMS provider và S3-compatible storage

## Tiêu chí hoàn thành

- Mỗi service sở hữu database và trạng thái riêng.
- Consumer xử lý event idempotent và không mất message.
- Notification service dùng contract trung lập ngôn ngữ, không phụ thuộc Java library.
- Mọi external call có timeout, retry budget và fallback rõ ràng.

## Nhật ký cải tiến

Ghi các cải tiến theo ngày tại thư mục `done/` với tên file `day-month.md`.
