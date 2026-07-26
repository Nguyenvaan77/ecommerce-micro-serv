# Phase 8 — AWS Production Deployment

## Mục tiêu

- Triển khai hệ thống lên AWS với khả năng mở rộng, quan sát và phục hồi.
- Tách rõ cấu hình local, staging và production.
- Tự động hóa build, kiểm thử và phát hành bằng CI/CD.

## Các bước đánh giá và triển khai

1. Chọn ECS hoặc EKS dựa trên yêu cầu vận hành và chi phí.
2. Thiết kế network, IAM, secrets, DNS, TLS và môi trường.
3. Thiết lập RDS, ElastiCache, MSK và S3.
4. Xây dựng image registry trên ECR và pipeline GitHub Actions.
5. Thiết lập migration, autoscaling, rollout và rollback.
6. Bổ sung monitoring, alerting, backup và disaster recovery.
7. Thực hiện security, load và resilience test trước production.
8. Chỉ triển khai hạ tầng sau khi kế hoạch chi tiết được người dùng đồng ý.

## Phạm vi

- AWS networking và IAM
- ECS hoặc EKS
- RDS, ElastiCache, MSK, S3 và ECR
- GitHub Actions CI/CD
- Observability, backup và disaster recovery

## Tiêu chí hoàn thành

- Hạ tầng được quản lý bằng Infrastructure as Code.
- Secret không nằm trong source code hoặc image.
- Deployment hỗ trợ health check, rollback và zero/minimal downtime.
- Có dashboard, alert, backup và tài liệu xử lý sự cố.

## Nhật ký cải tiến

Ghi các cải tiến theo ngày tại thư mục `done/` với tên file `day-month.md`.
