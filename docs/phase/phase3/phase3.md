# Phase 3 — Core Business Microservices

## Mục tiêu

- Xây dựng các bounded context cốt lõi: Product, Inventory và Order.
- Bảo đảm mỗi service sở hữu dữ liệu riêng và có thể triển khai độc lập.
- Thiết kế luồng đặt hàng an toàn trước race condition và lỗi phân tán.

## Các bước đánh giá và triển khai

1. Xác định domain model, aggregate, API và quyền sở hữu dữ liệu của từng service.
2. Thiết kế contract REST và Kafka event trước khi viết implementation.
3. Triển khai `product-service` và quản lý danh mục, sản phẩm.
4. Triển khai `inventory-service`, kiểm soát đồng thời và tồn kho.
5. Triển khai `order-service` cùng Saga cho luồng tạo đơn và giữ hàng.
6. Bổ sung timeout, retry, idempotency, tracing và kiểm thử tích hợp.
7. Chỉ triển khai từng service sau khi kế hoạch chi tiết được người dùng đồng ý.

## Phạm vi

- `product-service`
- `inventory-service`
- `order-service`
- REST/gRPC cho truy vấn đồng bộ khi cần
- Kafka cho luồng bất đồng bộ và cross-aggregate
- Saga và eventual consistency

## Tiêu chí hoàn thành

- Không service nào truy cập trực tiếp database của service khác.
- Contract API và event được version hóa.
- Luồng đặt hàng xử lý được retry, duplicate event và compensation.
- Có unit test, integration test và kịch bản lỗi phân tán.

## Nhật ký cải tiến

Ghi các cải tiến theo ngày tại thư mục `done/` với tên file `day-month.md`.
