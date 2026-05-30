# Hướng dẫn Setup Dự án (cho người mới)

## Yêu cầu cài đặt trước

1. **Java JDK 17+** — [Tải tại đây](https://adoptium.net/)
2. **Apache Maven** — [Tải tại đây](https://maven.apache.org/download.cgi)

> Kiểm tra đã cài chưa bằng cách mở Terminal/CMD chạy:
> ```
> java -version
> mvn -version
> ```

---

## Bước 1: Clone dự án

```bash
git clone <repository-url>
cd 2025.2-166155-06
```

---

## Bước 2: Tạo file `.env`

Trong thư mục gốc của dự án, tạo file `.env` bằng cách copy từ file mẫu:

**Windows (CMD):**
```cmd
copy .env.example .env
```

**Windows (PowerShell):**
```powershell
Copy-Item .env.example .env
```

**macOS / Linux:**
```bash
cp .env.example .env
```

---

## Bước 3: Điền thông tin kết nối Database

Mở file `.env` vừa tạo và thay đổi `DATABASE_URL` bằng connection string Aiven thật:

```env
DATABASE_URL="postgres://avnadmin:YOUR_PASSWORD@your-host.aivencloud.com:15759/defaultdb?sslmode=require"
```

### Lấy connection string ở đâu?

1. Đăng nhập [Aiven Console](https://console.aiven.io/)
2. Chọn **Service** PostgreSQL đang dùng
3. Vào tab **Overview**
4. Tìm phần **Connection information** → Copy **Service URI**
5. Dán vào `DATABASE_URL` trong file `.env`

> ⚠️ **Lưu ý:** File `.env` đã được thêm vào `.gitignore`, KHÔNG bị push lên Git. Mỗi người tự tạo file `.env` riêng trên máy mình.

---

## Bước 4: Build dự án

```bash
mvn compile
```

---

## Bước 5: Chạy ứng dụng

```bash
mvn javafx:run
```

---

## Tài khoản đăng nhập mặc định

| Username | Password | Role | Mô tả |
|----------|----------|------|--------|
| `admin` | `admin123` | admin | Quản trị viên |
| `banhang` | `banhang123` | sales | Bán hàng |
| `dathangquocte` | `dathangquocte123` | overseas | Đặt hàng quốc tế |
| `quanlykho` | `quanlykho123` | warehouse | Quản lý kho |
| `site` | `site123` | site | Site |

> Lưu ý: Tài khoản trên database Aiven có thể khác. Hãy kiểm tra bảng `users` trên Aiven.

---

## Xử lý lỗi thường gặp

### Lỗi: "No .env file found, using defaults"
→ Bạn chưa tạo file `.env`. Quay lại **Bước 2**.

### Lỗi: "SSL error: PKIX path building failed"
→ File `Database.java` đã xử lý SSL tự động. Nếu vẫn lỗi, kiểm tra lại `DATABASE_URL` có chứa `?sslmode=require` ở cuối.

### Lỗi: "Connection refused"
→ Kiểm tra lại host, port, username, password trong `DATABASE_URL`.

### Lỗi: "BUILD FAILURE" khi chạy `mvn javafx:run`
→ Đảm bảo chạy lệnh ở **thư mục gốc** của dự án (nơi có file `pom.xml`).
