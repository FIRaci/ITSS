package com.system.infrastructure.persistence;

import com.system.domain.request.ImportRequest;
import com.system.domain.request.RequestDetail;
import com.itss.ImportRequestHistory; // Keep using this or define new Domain entity? Wait, the domain has ImportRequest, RequestDetail, ChangeLog.
// Let's create ChangeLog domain class or reuse old ones.
// I will just use the legacy DTOs for now in com.itss since this is an iterative refactoring, or I should map them.
import com.itss.ImportRequestDetail;
import com.itss.ImportRequestHistory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RequestRepositoryImpl {

    public ObservableList<com.itss.ImportRequest> findAllMaster(String keyword) {
        ObservableList<com.itss.ImportRequest> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM ycnh WHERE LOWER(id) LIKE ? ORDER BY created_at DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new com.itss.ImportRequest(rs.getString("id"), rs.getString("status"), 
                        rs.getBoolean("is_accepted"), rs.getString("created_by"), rs.getString("created_at")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public ObservableList<com.itss.ImportRequestDetail> findDetailsByRequestId(String requestId) {
        ObservableList<com.itss.ImportRequestDetail> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM ycnh_chitiet WHERE ycnh_id = ? ORDER BY id ASC";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, requestId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new com.itss.ImportRequestDetail(rs.getInt("id"), rs.getString("ycnh_id"), 
                        rs.getString("merchandise_code"), rs.getInt("quantity"), 
                        rs.getString("unit"), String.valueOf(rs.getDate("desired_delivery_date"))));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public ObservableList<com.itss.ImportRequestHistory> findAllHistory() {
        ObservableList<com.itss.ImportRequestHistory> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM ycnh_history ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new com.itss.ImportRequestHistory(rs.getInt("id"), rs.getString("ycnh_id"), rs.getString("action_type"),
                        rs.getString("changed_by"), rs.getString("diff_text"), rs.getString("reason"), rs.getString("changed_at")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public void deleteImportRequest(String id) throws Exception {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete children first to prevent Foreign Key constraints and orphans
                try(PreparedStatement ps1 = conn.prepareStatement("DELETE FROM ycnh_chitiet WHERE ycnh_id = ?")) {
                    ps1.setString(1, id);
                    ps1.executeUpdate();
                }
                try(PreparedStatement ps2 = conn.prepareStatement("DELETE FROM ycnh_history WHERE ycnh_id = ?")) {
                    ps2.setString(1, id);
                    ps2.executeUpdate();
                }
                // Delete master record
                try(PreparedStatement ps3 = conn.prepareStatement("DELETE FROM ycnh WHERE id = ?")) {
                    ps3.setString(1, id);
                    ps3.executeUpdate();
                }
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public com.itss.ImportRequest findMasterById(String id) {
        String sql = "SELECT * FROM ycnh WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new com.itss.ImportRequest(rs.getString("id"), rs.getString("status"),
                        rs.getBoolean("is_accepted"), rs.getString("created_by"), rs.getString("created_at"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public ObservableList<com.itss.InternationalOrder> findOrdersByRequestId(String requestId) {
        ObservableList<com.itss.InternationalOrder> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM international_orders WHERE ycnh_id = ? ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, requestId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new com.itss.InternationalOrder(
                    rs.getInt("id"), rs.getString("ycnh_id"), rs.getString("site_code"),
                    rs.getString("merchandise_code"), rs.getInt("qty"),
                    rs.getString("shipping_method"), rs.getString("status")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public void updateImportRequestStatus(String requestId, String status) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE ycnh SET status = ? WHERE id = ?")) {
            ps.setString(1, status);
            ps.setString(2, requestId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void clearDraft(String requestId) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE ycnh_chitiet SET quantity = quantity WHERE ycnh_id = ?")) {
            ps.setString(1, requestId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void insertNewRequest(String reqId, String user, List<com.itss.ImportRequestDetail> detailsList) throws Exception {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Insert Master
                String sqlMaster = "INSERT INTO ycnh (id, created_by) VALUES (?, ?)";
                try(PreparedStatement psM = conn.prepareStatement(sqlMaster)) {
                    psM.setString(1, reqId); psM.setString(2, user); psM.executeUpdate();
                }

                // 2. Insert Details
                String sqlDetail = "INSERT INTO ycnh_chitiet (ycnh_id, merchandise_code, quantity, unit, desired_delivery_date) VALUES (?, ?, ?, ?, ?)";
                try(PreparedStatement psD = conn.prepareStatement(sqlDetail)) {
                    for(com.itss.ImportRequestDetail ct : detailsList) {
                        psD.setString(1, reqId);
                        psD.setString(2, ct.getMerchandiseCode());
                        psD.setInt(3, ct.getQuantity());
                        psD.setString(4, ct.getUnit());
                        psD.setDate(5, java.sql.Date.valueOf(LocalDate.parse(ct.getDesiredDeliveryDate())));
                        psD.addBatch();
                    }
                    psD.executeBatch();
                }

                // 3. Log History
                String sqlLog = "INSERT INTO ycnh_history (ycnh_id, action_type, changed_by, diff_text, reason) VALUES (?, ?, ?, ?, ?)";
                try(PreparedStatement psL = conn.prepareStatement(sqlLog)) {
                    psL.setString(1, reqId); psL.setString(2, "TẠO MỚI"); psL.setString(3, user); 
                    psL.setString(4, "Tạo mới yêu cầu với " + detailsList.size() + " mặt hàng.");
                    psL.setString(5, "Tạo mới"); psL.executeUpdate();
                }

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void updateRequest(String reqId, List<com.itss.ImportRequestDetail> inserts, List<com.itss.ImportRequestDetail> updates, List<com.itss.ImportRequestDetail> deletes, String diffText, String reason, String user) throws Exception {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete
                try(PreparedStatement psD = conn.prepareStatement("DELETE FROM ycnh_chitiet WHERE id = ?")) {
                    for(com.itss.ImportRequestDetail c : deletes) { psD.setInt(1, c.getId()); psD.addBatch(); }
                    psD.executeBatch();
                }
                // Update
                try(PreparedStatement psU = conn.prepareStatement("UPDATE ycnh_chitiet SET quantity=?, desired_delivery_date=? WHERE id=?")) {
                    for(com.itss.ImportRequestDetail c : updates) { 
                        psU.setInt(1, c.getQuantity()); psU.setDate(2, java.sql.Date.valueOf(c.getDesiredDeliveryDate()));
                        psU.setInt(3, c.getId()); psU.addBatch(); 
                    }
                    psU.executeBatch();
                }
                // Add
                try(PreparedStatement psA = conn.prepareStatement("INSERT INTO ycnh_chitiet (ycnh_id, merchandise_code, quantity, unit, desired_delivery_date) VALUES (?, ?, ?, ?, ?)")) {
                    for(com.itss.ImportRequestDetail c : inserts) {
                        psA.setString(1, reqId); psA.setString(2, c.getMerchandiseCode()); psA.setInt(3, c.getQuantity());
                        psA.setString(4, c.getUnit()); psA.setDate(5, java.sql.Date.valueOf(c.getDesiredDeliveryDate()));
                        psA.addBatch();
                    }
                    psA.executeBatch();
                }
                // Log
                try(PreparedStatement psL = conn.prepareStatement("INSERT INTO ycnh_history (ycnh_id, action_type, changed_by, diff_text, reason) VALUES (?, ?, ?, ?, ?)")) {
                    psL.setString(1, reqId); psL.setString(2, "CHỈNH SỬA"); psL.setString(3, user);
                    psL.setString(4, diffText); psL.setString(5, reason); psL.executeUpdate();
                }
                conn.commit();
            } catch(Exception e) { 
                conn.rollback(); 
                throw e; 
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
