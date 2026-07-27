# Phase 3 — Core Business Microservices

## Trạng thái

- Milestone hiện tại: `Phase 3A — user-service`.
- Quyết định triển khai `user-service` trước `auth-service`: `ĐÃ XÁC NHẬN`.
- Kế hoạch chi tiết: `APPROVED — ĐÃ XÁC NHẬN`.
- Implementation `user-service`: `COMPLETED — USER CONFIRMED`.
- Thời gian nghiệm thu Phase 3A: `16:02:28 - 27/07/2026`.
- Kế hoạch: [user-service-implementation-plan.md](user-service-implementation-plan.md).

## Mục tiêu

- Xây dựng các bounded context cốt lõi: User Profile, Product, Inventory và Order.
- Bảo đảm mỗi service sở hữu dữ liệu riêng và có thể triển khai độc lập.
- Thiết kế luồng đặt hàng an toàn trước race condition và lỗi phân tán.

## Các bước đánh giá và triển khai

1. Xác định domain model, aggregate, API và quyền sở hữu dữ liệu của từng service.
2. Triển khai `user-service` trước để quản lý hồ sơ và business role; chưa triển khai authentication.
3. Thiết kế contract REST và Kafka event trước khi viết implementation.
4. Triển khai `product-service` và quản lý danh mục, sản phẩm.
5. Triển khai `inventory-service`, kiểm soát đồng thời và tồn kho.
6. Triển khai `order-service` cùng Saga cho luồng tạo đơn và giữ hàng.
7. Bổ sung timeout, retry, idempotency, tracing và kiểm thử tích hợp khi có consumer thực tế.
8. Chỉ triển khai từng service sau khi kế hoạch chi tiết được người dùng đồng ý.

## Phạm vi

- `user-service`
- `product-service`
- `inventory-service`
- `order-service`
- REST/gRPC cho truy vấn đồng bộ khi cần
- Kafka cho luồng bất đồng bộ và cross-aggregate
- Saga và eventual consistency

## Tiêu chí hoàn thành

- Không service nào truy cập trực tiếp database của service khác.
- Contract API và event được version hóa.
- `user-service` chỉ quản lý hồ sơ/business role, không chứa password, token hoặc logic đăng nhập.
- Luồng đặt hàng xử lý được retry, duplicate event và compensation.
- Có unit test, integration test và kịch bản lỗi phân tán.

## Nhật ký cải tiến

Ghi các cải tiến theo ngày tại thư mục `done/` với tên file `day-month.md`.

- [25-07.md](done/25-07.md): khởi tạo cấu trúc tài liệu.
- [27-07.md](done/27-07.md): chọn `user-service` làm milestone đầu và lập kế hoạch chi tiết.
