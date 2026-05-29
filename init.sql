SET client_encoding = 'UTF8';

DROP TABLE IF EXISTS ycnh_history CASCADE;
DROP TABLE IF EXISTS ycnh_chitiet CASCADE;
DROP TABLE IF EXISTS ycnh CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS site_transport_log CASCADE;
DROP TABLE IF EXISTS cancellation_requests CASCADE;
DROP TABLE IF EXISTS discrepancy_items CASCADE;
DROP TABLE IF EXISTS discrepancy_reports CASCADE;
DROP TABLE IF EXISTS order_status_history CASCADE;

-- Users table (5 roles)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL,
    site_code VARCHAR(50)
);

INSERT INTO users (username, password, role) VALUES
('banhang', 'banhang123', 'Bán hàng'),
('dathangquocte', 'dathangquocte123', 'Đặt hàng quốc tế'),
('quanlykho', 'quanlykho123', 'Quản lý kho'),
('site', 'site123', 'Site'),
('admin', 'admin123', 'Admin');

-- Bảng lưu thông tin Yêu Cầu Nhập Hàng (Order Master)
CREATE TABLE ycnh (
    id VARCHAR(50) PRIMARY KEY, -- Mã yêu cầu (VD: REQ-2026-001)
    status VARCHAR(50) NOT NULL DEFAULT 'Chờ tiếp nhận',
    is_accepted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng chi tiết mặt hàng trong một Yêu Cầu Nhập Hàng (Order Details)
CREATE TABLE ycnh_chitiet (
    id SERIAL PRIMARY KEY,
    ycnh_id VARCHAR(50) REFERENCES ycnh(id) ON DELETE CASCADE,
    merchandise_code VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    unit VARCHAR(50) NOT NULL,
    desired_delivery_date DATE NOT NULL
);

-- Dữ liệu mẫu (1 YCNH chứa 2 mặt hàng)
INSERT INTO ycnh (id, status, created_by) VALUES
('REQ-001', 'Chờ tiếp nhận', 'banhang');

INSERT INTO ycnh_chitiet (ycnh_id, merchandise_code, quantity, unit, desired_delivery_date) VALUES
('REQ-001', 'MH-2049', 500, 'Thùng', '2026-08-15'),
('REQ-001', 'MH-2050', 200, 'Cái', '2026-08-20');

-- Bảng lưu lịch sử chỉnh sửa (Log Version)
CREATE TABLE ycnh_history (
    id SERIAL PRIMARY KEY,
    ycnh_id VARCHAR(50) REFERENCES ycnh(id) ON DELETE CASCADE,
    action_type VARCHAR(50) NOT NULL,
    changed_by VARCHAR(50) NOT NULL,
    diff_text TEXT NOT NULL,
    reason TEXT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================= CÁC BẢNG CHO ROLE KHÁC =================

-- Bảng quản lý Site
CREATE TABLE sites (
    id SERIAL PRIMARY KEY,
    site_code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    days_ship INT NOT NULL,
    days_air INT NOT NULL,
    other_info TEXT
);

-- Tồn kho của Site (Dùng cho Thuật toán phân bổ)
CREATE TABLE site_inventory (
    id SERIAL PRIMARY KEY,
    site_code VARCHAR(50) REFERENCES sites(site_code) ON DELETE CASCADE,
    merchandise_code VARCHAR(100) NOT NULL,
    stock_qty INT DEFAULT 0
);

-- Đơn hàng Quốc tế (Gửi cho Site)
CREATE TABLE international_orders (
    id SERIAL PRIMARY KEY,
    ycnh_id VARCHAR(50) REFERENCES ycnh(id),
    site_code VARCHAR(50) REFERENCES sites(site_code),
    merchandise_code VARCHAR(100),
    qty INT,
    shipping_method VARCHAR(20),
    status VARCHAR(50) DEFAULT 'Đã đặt hàng',
    discrepancy_note TEXT
);

-- Log thay đổi trạng thái đơn hàng quốc tế
CREATE TABLE order_status_history (
    id SERIAL PRIMARY KEY,
    order_id INT REFERENCES international_orders(id) ON DELETE CASCADE,
    old_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    note TEXT,
    changed_by VARCHAR(50),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Log cập nhật thông tin vận chuyển Site
CREATE TABLE site_transport_log (
    id SERIAL PRIMARY KEY,
    site_code VARCHAR(50) REFERENCES sites(site_code) ON DELETE CASCADE,
    old_days_ship INT,
    new_days_ship INT,
    old_days_air INT,
    new_days_air INT,
    note TEXT,
    changed_by VARCHAR(50),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Yêu cầu hủy hoặc xử lý sự cố đơn hàng
CREATE TABLE cancellation_requests (
    id SERIAL PRIMARY KEY,
    order_id INT REFERENCES international_orders(id) ON DELETE CASCADE,
    reason TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'Chờ xử lý',
    created_by VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    handled_by VARCHAR(50),
    handled_at TIMESTAMP
);

-- Biên bản sai lệch hàng hóa
CREATE TABLE discrepancy_reports (
    id SERIAL PRIMARY KEY,
    order_id INT REFERENCES international_orders(id) ON DELETE CASCADE,
    ycnh_id VARCHAR(50) REFERENCES ycnh(id),
    site_code VARCHAR(50) REFERENCES sites(site_code),
    note TEXT,
    evidence_path TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'Chờ xử lý sai lệch',
    created_by VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE discrepancy_items (
    id SERIAL PRIMARY KEY,
    report_id INT REFERENCES discrepancy_reports(id) ON DELETE CASCADE,
    merchandise_code VARCHAR(100) NOT NULL,
    qty_reported INT NOT NULL,
    reason VARCHAR(100) NOT NULL
);

-- Dữ liệu mẫu
INSERT INTO sites (site_code, name, days_ship, days_air, other_info) VALUES
('SITE-US', 'Amazon Global Supply', 30, 5, 'Kho USA'),
('SITE-CH', 'Alibaba Export', 15, 3, 'Kho China');

INSERT INTO site_inventory (site_code, merchandise_code, stock_qty) VALUES
('SITE-US', 'MH-2049', 1000),
('SITE-US', 'MH-2050', 50),
('SITE-CH', 'MH-2050', 500);

-- Update site user mapping
UPDATE users SET site_code = 'SITE-US' WHERE username = 'site';
