-- ============================================================
-- RetailApp — Database Reset & Seed Script
-- DB: ITSS (PostgreSQL)
-- Run: psql -U postgres -d ITSS -f seed.sql
-- ============================================================

-- ── 1. DROP (đúng thứ tự FK) ─────────────────────────────────
DROP TABLE IF EXISTS discrepancy_reports    CASCADE;
DROP TABLE IF EXISTS international_orders   CASCADE;
DROP TABLE IF EXISTS ycnh_history           CASCADE;
DROP TABLE IF EXISTS ycnh_chitiet           CASCADE;
DROP TABLE IF EXISTS ycnh                   CASCADE;
DROP TABLE IF EXISTS site_inventory         CASCADE;
DROP TABLE IF EXISTS sites                  CASCADE;
DROP TABLE IF EXISTS merchandise_catalog    CASCADE;
DROP TABLE IF EXISTS users                  CASCADE;

-- ── 2. CREATE TABLES ─────────────────────────────────────────

-- Users (hỗ trợ đa site)
CREATE TABLE users (
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(50)  NOT NULL,  -- admin | sales | overseas | warehouse | site
    site_code  VARCHAR(20)  DEFAULT NULL
);

-- Danh mục mặt hàng (do Sales quản lý)
CREATE TABLE merchandise_catalog (
    code        VARCHAR(50)  PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    unit        VARCHAR(50)  NOT NULL,
    description TEXT         DEFAULT ''
);

-- Sites (đối tác nước ngoài)
CREATE TABLE sites (
    id         SERIAL PRIMARY KEY,
    site_code  VARCHAR(20)  UNIQUE NOT NULL,
    name       VARCHAR(200) NOT NULL,
    days_ship  INT          NOT NULL DEFAULT 30,
    days_air   INT          NOT NULL DEFAULT 7,
    other_info TEXT         DEFAULT ''
);

-- Tồn kho tại site
CREATE TABLE site_inventory (
    id               SERIAL PRIMARY KEY,
    site_code        VARCHAR(20)  NOT NULL REFERENCES sites(site_code) ON DELETE CASCADE,
    merchandise_code VARCHAR(50)  NOT NULL REFERENCES merchandise_catalog(code) ON DELETE CASCADE,
    stock_qty        INT          NOT NULL DEFAULT 0,
    UNIQUE(site_code, merchandise_code)
);

-- Yêu cầu nhập hàng (master)
CREATE TABLE ycnh (
    id          VARCHAR(50)  PRIMARY KEY,
    status      VARCHAR(50)  NOT NULL DEFAULT 'Chờ duyệt',
    is_accepted BOOLEAN      NOT NULL DEFAULT FALSE,
    created_by  VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Chi tiết yêu cầu nhập hàng
CREATE TABLE ycnh_chitiet (
    id                    SERIAL      PRIMARY KEY,
    ycnh_id               VARCHAR(50) NOT NULL REFERENCES ycnh(id) ON DELETE CASCADE,
    merchandise_code      VARCHAR(50) NOT NULL REFERENCES merchandise_catalog(code),
    quantity              INT         NOT NULL CHECK (quantity > 0),
    unit                  VARCHAR(50) NOT NULL,
    desired_delivery_date DATE        NOT NULL
);

-- Lịch sử chỉnh sửa yêu cầu
CREATE TABLE ycnh_history (
    id          SERIAL       PRIMARY KEY,
    ycnh_id     VARCHAR(50)  NOT NULL,
    action_type VARCHAR(50)  NOT NULL,   -- CREATE | EDIT | CANCEL | ACCEPT
    changed_by  VARCHAR(100) NOT NULL,
    diff_text   TEXT         DEFAULT '',
    reason      TEXT         DEFAULT '',
    changed_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Đơn hàng quốc tế (đặt từ site)
CREATE TABLE international_orders (
    id               SERIAL       PRIMARY KEY,
    ycnh_id          VARCHAR(50)  REFERENCES ycnh(id),
    site_code        VARCHAR(20)  NOT NULL,
    merchandise_code VARCHAR(50)  NOT NULL,
    qty              INT          NOT NULL,
    shipping_method  VARCHAR(50)  NOT NULL DEFAULT 'Đường Biển',
    status           VARCHAR(50)  NOT NULL DEFAULT 'Đã đặt hàng'
    -- status values: Đã đặt hàng | Đang giao | Đã nhập kho | Sai lệch
);

-- Biên bản sai lệch
CREATE TABLE discrepancy_reports (
    id               SERIAL       PRIMARY KEY,
    order_id         INT          NOT NULL REFERENCES international_orders(id),
    reason           VARCHAR(200) NOT NULL,
    qty_discrepancy  INT          NOT NULL,
    evidence_path    TEXT         DEFAULT '',
    note             TEXT         DEFAULT '',
    reported_by      VARCHAR(100) NOT NULL,
    reported_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ── 3. SEED: USERS ───────────────────────────────────────────
-- Mật khẩu tất cả là "admin123" (SHA-256)
-- SHA256("admin123") = 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
INSERT INTO users (username, password, role, site_code) VALUES
('admin',     '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin',     NULL),
('sales1',    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'sales',     NULL),
('sales2',    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'sales',     NULL),
('overseas1', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'overseas',  NULL),
('warehouse1','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'warehouse', NULL),
('site_jp',   '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'site',      'JP-TYO'),
('site_kr',   '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'site',      'KR-SEL'),
('site_cn',   '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'site',      'CN-SHA'),
('site_th',   '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'site',      'TH-BKK'),
('site_sg',   '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'site',      'SG-SIN');

-- ── 4. SEED: MERCHANDISE CATALOG ─────────────────────────────
INSERT INTO merchandise_catalog (code, name, unit, description) VALUES
('MH001', 'Áo thun nam cao cấp',         'cái',  'Áo thun nam cotton 100%, nhiều màu sắc, co giãn 4 chiều'),
('MH002', 'Quần jean nữ thời trang',      'cái',  'Quần jean nữ skinny, vải denim cao cấp, co giãn tốt'),
('MH003', 'Giày thể thao unisex',         'đôi',  'Giày thể thao đa năng, đế cao su chống trượt, thoáng khí'),
('MH004', 'Túi xách tay da bò',           'cái',  'Túi xách da bò thật, nhiều ngăn tiện dụng, khóa kim loại'),
('MH005', 'Áo khoác nỉ nữ',              'cái',  'Áo khoác nỉ lông cừu, giữ ấm tốt, chống gió nhẹ'),
('MH006', 'Kính mát chống UV400',         'cái',  'Kính mát phân cực, chống tia UV400, gọng hợp kim'),
('MH007', 'Đồng hồ thời trang nam',       'cái',  'Đồng hồ cơ automatic, dây da bò, chống nước 50m'),
('MH008', 'Balo học sinh đa năng',        'cái',  'Balo chống nước, nhiều ngăn, ngăn chống sốc laptop 15.6"'),
('MH009', 'Vớ cotton thể thao',           'đôi',  'Vớ cotton cao cổ, thấm hút tốt, đàn hồi cao'),
('MH010', 'Thắt lưng da nam',             'cái',  'Thắt lưng da bò thật, khóa inox, nhiều kích cỡ'),
('MH011', 'Áo sơ mi nam công sở',         'cái',  'Áo sơ mi vải lụa tơ tằm, không nhăn, dễ ủi'),
('MH012', 'Váy đầm dự tiệc',              'cái',  'Đầm voan phồng, thiết kế sang trọng, nhiều màu'),
('MH013', 'Mũ bông len mùa đông',         'cái',  'Mũ len dệt thủ công, giữ ấm, phong cách Hàn Quốc'),
('MH014', 'Ví da nữ cao cấp',             'cái',  'Ví da thật nhiều ngăn đựng thẻ, thiết kế gọn nhẹ'),
('MH015', 'Dây nịt thể thao unisex',      'cái',  'Dây nịt nylon nhẹ, khóa tự động, phù hợp hoạt động ngoài trời');

-- ── 5. SEED: SITES ───────────────────────────────────────────
INSERT INTO sites (site_code, name, days_ship, days_air, other_info) VALUES
('JP-TYO', 'Tokyo Distribution Center',  25, 5,  'Liên hệ: logistics@tokyo-dc.jp | Địa chỉ: 1-1 Shinjuku, Tokyo'),
('KR-SEL', 'Seoul Fashion Hub',          20, 4,  'Liên hệ: ops@seoulhub.kr | Địa chỉ: 100 Gangnam-gu, Seoul'),
('CN-SHA', 'Shanghai Trade Center',      18, 3,  'Liên hệ: trade@shctrade.cn | Địa chỉ: 88 Pudong Ave, Shanghai'),
('TH-BKK', 'Bangkok Import Depot',       22, 5,  'Liên hệ: import@bkkdepot.th | Địa chỉ: 55 Siam Sq, Bangkok'),
('SG-SIN', 'Singapore Logistics Hub',    15, 3,  'Liên hệ: hub@sglogistics.sg | Địa chỉ: 10 Jurong East, Singapore');

-- ── 6. SEED: SITE INVENTORY ──────────────────────────────────
-- JP-TYO
INSERT INTO site_inventory (site_code, merchandise_code, stock_qty) VALUES
('JP-TYO', 'MH001', 500), ('JP-TYO', 'MH002', 300), ('JP-TYO', 'MH003', 250),
('JP-TYO', 'MH007', 150), ('JP-TYO', 'MH008', 200), ('JP-TYO', 'MH011', 400),
('JP-TYO', 'MH013', 600), ('JP-TYO', 'MH014', 180);

-- KR-SEL
INSERT INTO site_inventory (site_code, merchandise_code, stock_qty) VALUES
('KR-SEL', 'MH001', 350), ('KR-SEL', 'MH002', 450), ('KR-SEL', 'MH005', 300),
('KR-SEL', 'MH006', 200), ('KR-SEL', 'MH012', 250), ('KR-SEL', 'MH013', 400),
('KR-SEL', 'MH015', 320);

-- CN-SHA
INSERT INTO site_inventory (site_code, merchandise_code, stock_qty) VALUES
('CN-SHA', 'MH001', 800), ('CN-SHA', 'MH002', 600), ('CN-SHA', 'MH003', 500),
('CN-SHA', 'MH004', 200), ('CN-SHA', 'MH009', 1000), ('CN-SHA', 'MH010', 400),
('CN-SHA', 'MH011', 700), ('CN-SHA', 'MH015', 500);

-- TH-BKK
INSERT INTO site_inventory (site_code, merchandise_code, stock_qty) VALUES
('TH-BKK', 'MH003', 300), ('TH-BKK', 'MH004', 150), ('TH-BKK', 'MH006', 250),
('TH-BKK', 'MH008', 180), ('TH-BKK', 'MH009', 500), ('TH-BKK', 'MH012', 200);

-- SG-SIN
INSERT INTO site_inventory (site_code, merchandise_code, stock_qty) VALUES
('SG-SIN', 'MH001', 200), ('SG-SIN', 'MH005', 150), ('SG-SIN', 'MH007', 120),
('SG-SIN', 'MH010', 300), ('SG-SIN', 'MH014', 250), ('SG-SIN', 'MH015', 180);

-- ── 7. SEED: YCNH (Yêu Cầu Nhập Hàng) ───────────────────────
INSERT INTO ycnh (id, status, is_accepted, created_by, created_at) VALUES
('YCNH-20250510-001', 'Đã duyệt',  TRUE,  'sales1',    '2025-05-10 08:30:00'),
('YCNH-20250512-001', 'Đã duyệt',  TRUE,  'sales2',    '2025-05-12 09:15:00'),
('YCNH-20250515-001', 'Chờ duyệt', FALSE, 'sales1',    '2025-05-15 10:00:00'),
('YCNH-20250518-001', 'Chờ duyệt', FALSE, 'sales2',    '2025-05-18 14:30:00'),
('YCNH-20250520-001', 'Đã huỷ',    FALSE, 'sales1',    '2025-05-20 11:00:00'),
('YCNH-20250522-001', 'Đã duyệt',  TRUE,  'sales1',    '2025-05-22 09:00:00'),
('YCNH-20250524-001', 'Chờ duyệt', FALSE, 'sales2',    '2025-05-24 16:00:00'),
('YCNH-20250526-001', 'Đã duyệt',  TRUE,  'sales1',    '2025-05-26 08:45:00');

-- ── 8. SEED: YCNH_CHITIET ─────────────────────────────────────
INSERT INTO ycnh_chitiet (ycnh_id, merchandise_code, quantity, unit, desired_delivery_date) VALUES
-- YCNH-20250510-001
('YCNH-20250510-001', 'MH001', 200, 'cái',  '2025-06-15'),
('YCNH-20250510-001', 'MH003', 100, 'đôi',  '2025-06-20'),
('YCNH-20250510-001', 'MH008', 50,  'cái',  '2025-06-18'),
-- YCNH-20250512-001
('YCNH-20250512-001', 'MH002', 150, 'cái',  '2025-06-25'),
('YCNH-20250512-001', 'MH006', 80,  'cái',  '2025-06-22'),
-- YCNH-20250515-001
('YCNH-20250515-001', 'MH004', 60,  'cái',  '2025-07-01'),
('YCNH-20250515-001', 'MH007', 40,  'cái',  '2025-07-05'),
-- YCNH-20250518-001
('YCNH-20250518-001', 'MH009', 300, 'đôi',  '2025-07-10'),
('YCNH-20250518-001', 'MH010', 100, 'cái',  '2025-07-08'),
-- YCNH-20250520-001 (đã huỷ)
('YCNH-20250520-001', 'MH005', 120, 'cái',  '2025-07-15'),
-- YCNH-20250522-001
('YCNH-20250522-001', 'MH011', 200, 'cái',  '2025-07-20'),
('YCNH-20250522-001', 'MH013', 300, 'cái',  '2025-07-18'),
-- YCNH-20250524-001
('YCNH-20250524-001', 'MH012', 80,  'cái',  '2025-08-01'),
('YCNH-20250524-001', 'MH014', 50,  'cái',  '2025-07-28'),
-- YCNH-20250526-001
('YCNH-20250526-001', 'MH001', 100, 'cái',  '2025-08-05'),
('YCNH-20250526-001', 'MH015', 150, 'cái',  '2025-08-10');

-- ── 9. SEED: YCNH_HISTORY ────────────────────────────────────
INSERT INTO ycnh_history (ycnh_id, action_type, changed_by, diff_text, reason, changed_at) VALUES
('YCNH-20250510-001', 'CREATE', 'sales1',    'Tạo yêu cầu với 3 mặt hàng', '', '2025-05-10 08:30:00'),
('YCNH-20250510-001', 'ACCEPT', 'overseas1', 'Duyệt và tạo đơn hàng quốc tế', '', '2025-05-10 10:00:00'),
('YCNH-20250512-001', 'CREATE', 'sales2',    'Tạo yêu cầu với 2 mặt hàng', '', '2025-05-12 09:15:00'),
('YCNH-20250512-001', 'ACCEPT', 'overseas1', 'Duyệt sau khi kiểm tra tồn kho', '', '2025-05-12 11:30:00'),
('YCNH-20250515-001', 'CREATE', 'sales1',    'Tạo yêu cầu với 2 mặt hàng', '', '2025-05-15 10:00:00'),
('YCNH-20250518-001', 'CREATE', 'sales2',    'Tạo yêu cầu với 2 mặt hàng', '', '2025-05-18 14:30:00'),
('YCNH-20250520-001', 'CREATE', 'sales1',    'Tạo yêu cầu với 1 mặt hàng', '', '2025-05-20 11:00:00'),
('YCNH-20250520-001', 'CANCEL', 'sales1',    'Hủy do nhà cung cấp thay đổi giá', 'Giá tăng 30%, không còn phù hợp ngân sách', '2025-05-20 14:00:00'),
('YCNH-20250522-001', 'CREATE', 'sales1',    'Tạo yêu cầu với 2 mặt hàng', '', '2025-05-22 09:00:00'),
('YCNH-20250522-001', 'EDIT',   'sales1',    '[MH011] SL: 150 → 200', 'Cập nhật theo yêu cầu của bộ phận marketing', '2025-05-22 10:30:00'),
('YCNH-20250522-001', 'ACCEPT', 'overseas1', 'Duyệt sau khi kiểm tra', '', '2025-05-22 14:00:00'),
('YCNH-20250524-001', 'CREATE', 'sales2',    'Tạo yêu cầu với 2 mặt hàng', '', '2025-05-24 16:00:00'),
('YCNH-20250526-001', 'CREATE', 'sales1',    'Tạo yêu cầu với 2 mặt hàng', '', '2025-05-26 08:45:00'),
('YCNH-20250526-001', 'ACCEPT', 'overseas1', 'Duyệt khẩn vì hàng sắp hết tồn kho', '', '2025-05-26 09:30:00');

-- ── 10. SEED: INTERNATIONAL ORDERS ───────────────────────────
INSERT INTO international_orders (ycnh_id, site_code, merchandise_code, qty, shipping_method, status) VALUES
-- Từ YCNH-20250510-001 (đã duyệt)
('YCNH-20250510-001', 'CN-SHA', 'MH001', 200, 'Đường Biển', 'Đã nhập kho'),
('YCNH-20250510-001', 'CN-SHA', 'MH003', 100, 'Hàng Không', 'Đã nhập kho'),
('YCNH-20250510-001', 'JP-TYO', 'MH008',  50, 'Hàng Không', 'Đã nhập kho'),
-- Từ YCNH-20250512-001 (đã duyệt)
('YCNH-20250512-001', 'KR-SEL', 'MH002', 150, 'Đường Biển', 'Đang giao'),
('YCNH-20250512-001', 'KR-SEL', 'MH006',  80, 'Hàng Không', 'Đang giao'),
-- Từ YCNH-20250522-001 (đã duyệt)
('YCNH-20250522-001', 'JP-TYO', 'MH011', 200, 'Đường Biển', 'Đã đặt hàng'),
('YCNH-20250522-001', 'JP-TYO', 'MH013', 300, 'Hàng Không', 'Đã đặt hàng'),
-- Từ YCNH-20250526-001 (đã duyệt mới nhất)
('YCNH-20250526-001', 'CN-SHA', 'MH001', 100, 'Đường Biển', 'Đã đặt hàng'),
('YCNH-20250526-001', 'SG-SIN', 'MH015', 150, 'Hàng Không', 'Đã đặt hàng');

-- ── 11. SEED: DISCREPANCY REPORTS ────────────────────────────
-- Biên bản sai lệch cho đơn đã nhập kho (giả lập 1 case)
INSERT INTO discrepancy_reports (order_id, reason, qty_discrepancy, evidence_path, note, reported_by, reported_at) VALUES
(2, 'Thiếu hàng', 5, 'https://evidence.internal/photo001.jpg', 'Kiện hàng bị thiếu 5 đôi, đã kiểm đếm 3 lần', 'warehouse1', '2025-05-15 09:00:00');

-- ── VERIFY ───────────────────────────────────────────────────
SELECT 'users'               AS tbl, COUNT(*) AS rows FROM users
UNION ALL SELECT 'merchandise_catalog', COUNT(*) FROM merchandise_catalog
UNION ALL SELECT 'sites',               COUNT(*) FROM sites
UNION ALL SELECT 'site_inventory',      COUNT(*) FROM site_inventory
UNION ALL SELECT 'ycnh',                COUNT(*) FROM ycnh
UNION ALL SELECT 'ycnh_chitiet',        COUNT(*) FROM ycnh_chitiet
UNION ALL SELECT 'ycnh_history',        COUNT(*) FROM ycnh_history
UNION ALL SELECT 'international_orders',COUNT(*) FROM international_orders
UNION ALL SELECT 'discrepancy_reports', COUNT(*) FROM discrepancy_reports
ORDER BY tbl;
