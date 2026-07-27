# Phase 4 — API Gateway & Vận hành

## Mục tiêu

- Cung cấp một điểm vào thống nhất cho client qua Apache APISIX.
- Chuẩn hóa routing, xác thực, rate limiting và khả năng quan sát.
- Bảo vệ service nội bộ trước truy cập trái phép và lỗi dây chuyền.

## Các bước đánh giá và triển khai

1. Lập danh sách route public, protected và internal.
2. Thiết kế quy tắc route, version API và service discovery.
3. Tích hợp APISIX với Keycloak để xác thực JWT.
4. Thiết lập rate limiting, timeout, retry và circuit breaker phù hợp.
5. Truyền correlation ID và trace context qua gateway.
6. Bổ sung dashboard, metrics, access log và kịch bản kiểm thử.
7. Chỉ triển khai sau khi kế hoạch chi tiết được người dùng đồng ý.

## Phạm vi

- Apache APISIX
- Routing và API versioning
- JWT/OIDC với Keycloak
- Rate limiting và resilience
- Logging, metrics và distributed tracing

## Tiêu chí hoàn thành

- Client chỉ truy cập backend qua gateway đối với public API.
- Route được version hóa và kiểm thử tự động.
- Gateway từ chối token không hợp lệ và áp dụng rate limit.
- Có thể theo dõi một request xuyên suốt bằng correlation ID.

## Nhật ký cải tiến

Ghi các cải tiến theo ngày tại thư mục `done/` với tên file `day-month.md`.
