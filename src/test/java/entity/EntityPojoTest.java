package entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import entity.chung.*;

class EntityPojoTest {

    @Test
    void importRequestConstructorAndGetters() {
        ImportRequest r = new ImportRequest("REQ-001", "Chờ duyệt", false, "sales1", "2026-05-30");
        assertEquals("REQ-001", r.getId());
        assertEquals("Chờ duyệt", r.getStatus());
        assertFalse(r.isAccepted());
        assertEquals("sales1", r.getCreatedBy());
        assertEquals("2026-05-30", r.getCreatedAt());
    }

    @Test
    void importRequestSetters() {
        ImportRequest r = new ImportRequest("REQ-001", "Chờ duyệt", false, "sales1", "2026-05-30");
        r.setStatus("Đã duyệt");
        r.setAccepted(true);
        assertEquals("Đã duyệt", r.getStatus());
        assertTrue(r.isAccepted());
    }

    @Test
    void merchandiseConstructor() {
        Merchandise m = new Merchandise("MH001", "Áo thun", "cái", "Áo thun cotton");
        assertEquals("MH001", m.getCode());
        assertEquals("Áo thun", m.getName());
        assertEquals("cái", m.getUnit());
        assertEquals("Áo thun cotton", m.getDescription());
    }

    @Test
    void merchandiseDefaultConstructor() {
        Merchandise m = new Merchandise();
        m.setCode("MH002");
        m.setName("Quần jean");
        assertEquals("MH002", m.getCode());
        assertEquals("Quần jean", m.getName());
    }

    @Test
    void merchandiseToString() {
        Merchandise m = new Merchandise("MH001", "Áo thun", "cái", null);
        assertEquals("MH001 - Áo thun", m.toString());
    }

    @Test
    void userEntity() {
        User u = new User(1, "sales1", "pass123", "Bán hàng", null);
        assertEquals(1, u.getId());
        assertEquals("sales1", u.getUsername());
        assertEquals("Bán hàng", u.getRole());
        assertNull(u.getSiteCode());
    }

    @Test
    void siteEntity() {
        Site s = new Site(1, "SITE-US", "Kho Mỹ", 30, 5, "Ghi chú");
        assertEquals("SITE-US", s.getSiteCode());
        assertEquals("Kho Mỹ", s.getName());
        assertEquals(30, s.getDaysShip());
        assertEquals(5, s.getDaysAir());
    }

    @Test
    void importRequestDetail() {
        ImportRequestDetail d = new ImportRequestDetail(1, "REQ-001", "MH001", 100, "cái", "2026-06-15");
        assertEquals("REQ-001", d.getRequestId());
        assertEquals("MH001", d.getMerchandiseCode());
        assertEquals(100, d.getQuantity());
        assertEquals("cái", d.getUnit());
        assertEquals("2026-06-15", d.getDesiredDeliveryDate());
    }

    @Test
    void internationalOrder() {
        InternationalOrder o = new InternationalOrder(1, "REQ-001", "SITE-US", "MH001", 100, "Đường Biển", "Đã đặt hàng");
        assertEquals(1, o.getId());
        assertEquals("REQ-001", o.getRequestId());
        assertEquals("SITE-US", o.getSiteCode());
        assertEquals("MH001", o.getMerchandiseCode());
        assertEquals(100, o.getQty());
        assertEquals("Đường Biển", o.getShippingMethod());
        assertEquals("Đã đặt hàng", o.getStatus());
    }
}
