# Phase 7 — Frontend & Local Integration

## Mục tiêu

- Xây dựng frontend Next.js và tích hợp toàn bộ public API qua APISIX.
- Chạy hệ thống hoàn chỉnh trong môi trường local bằng Docker Compose.
- Xác nhận các business flow chính bằng kiểm thử end-to-end.

## Các bước đánh giá và triển khai

1. Xác định user journey, page, API contract và authentication flow.
2. Thiết kế cấu trúc Next.js, state management và error handling.
3. Tích hợp frontend với APISIX và Keycloak.
4. Đóng gói toàn bộ service bằng image tái lập được.
5. Hoàn thiện Docker Compose cho frontend, backend và infrastructure.
6. Viết E2E test cho các luồng nghiệp vụ quan trọng.
7. Chỉ triển khai sau khi kế hoạch chi tiết được người dùng đồng ý.

## Phạm vi

- Next.js frontend
- API Gateway integration
- Authentication flow
- Docker image và Docker Compose
- End-to-end testing

## Tiêu chí hoàn thành

- Các luồng đăng nhập, xem sản phẩm, đặt hàng và theo dõi đơn chạy end-to-end.
- Môi trường local có thể khởi động bằng tài liệu và command thống nhất.
- Health check xác nhận đúng trạng thái sẵn sàng của service.
- Có test tự động cho happy path và failure path quan trọng.

## Nhật ký cải tiến

Ghi các cải tiến theo ngày tại thư mục `done/` với tên file `day-month.md`.
