# PostgreSQL local runbook

## Phạm vi

Runbook này dành cho PostgreSQL local của Phase 2A:

- PostgreSQL 16.
- Superuser `postgres`.
- Database mặc định `postgres`.
- Không dùng cho shared environment hoặc production.

## Chuẩn bị

Lần đầu clone repository, tạo `.env` từ file mẫu:

```powershell
Copy-Item .env.example .env
```

Giá trị mặc định dành cho local:

```dotenv
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=postgres
POSTGRES_PORT=5432
```

Nếu port `5432` đã bị chiếm, sửa `POSTGRES_PORT` trong `.env`, ví dụ:

```dotenv
POSTGRES_PORT=55432
```

## Lệnh thường dùng

```powershell
make config
make up
make ps
make health
make verify
make logs
make restart
make down
```

- `make up`: pull image mới nhất trong nhánh PostgreSQL 16 rồi chạy container và chờ health check.
- `make down`: xóa container/network của Compose project nhưng giữ named volume.
- `make restart`: down, pull image rồi tạo lại container; dữ liệu vẫn được giữ.
- Thoát `make logs` bằng `Ctrl+C`; container vẫn tiếp tục chạy.

Nếu không dùng Make:

```powershell
docker compose config --quiet
docker compose pull postgres
docker compose up -d --wait postgres
docker compose ps
docker compose logs --follow postgres
docker compose down
```

## Kết nối

Từ máy host:

```text
Host: localhost
Port: giá trị POSTGRES_PORT trong .env
Database: postgres
Username: postgres
Password: postgres
```

JDBC URL mặc định:

```text
jdbc:postgresql://localhost:5432/postgres
```

Từ một service nằm trong cùng Compose project, dùng hostname `postgres` và container port `5432`.

Kiểm tra trực tiếp:

```powershell
docker compose exec -T postgres pg_isready -U postgres -d postgres
docker compose exec -T postgres psql -U postgres -d postgres -c "SELECT 1;"
```

## Dữ liệu và reset

`make down` và `make restart` không xóa dữ liệu vì PostgreSQL dùng named volume.

Lệnh sau xóa container, image PostgreSQL của project và toàn bộ database volume:

```powershell
make clear CONFIRM=YES
```

Không chạy lệnh này khi còn dữ liệu local cần giữ. `make clear` không có `CONFIRM=YES` sẽ chỉ cảnh báo và từ chối xóa.

## Xử lý lỗi nhanh

### Docker chưa chạy

Khởi động Docker Desktop rồi chạy lại:

```powershell
docker version
make up
```

### Port bị chiếm

Đổi `POSTGRES_PORT` trong `.env`, sau đó:

```powershell
make restart
```

### Container không healthy

```powershell
docker compose ps
docker compose logs postgres
```

### Muốn khởi tạo lại từ đầu

Chỉ khi chấp nhận mất toàn bộ dữ liệu local:

```powershell
make clear CONFIRM=YES
make up
```
