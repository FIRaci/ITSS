package com.itss;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

public class ImportRequestRepository {

    public ObservableList<ImportRequest> findAllMaster(String keyword) {
        ObservableList<ImportRequest> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM ImportRequest WHERE LOWER(id) LIKE ? ORDER BY created_at DESC";
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new ImportRequest(rs.getString("id"), rs.getString("status"), 
                        rs.getBoolean("is_accepted"), rs.getString("created_by"), rs.getString("created_at")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public ObservableList<ImportRequestDetail> findDetailsByRequestId(String requestId) {
        ObservableList<ImportRequestDetail> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM ycnh_chitiet WHERE ycnh_id = ? ORDER BY id ASC";
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, requestId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new ImportRequestDetail(rs.getInt("id"), rs.getString("ycnh_id"), 
                        rs.getString("merchandise_code"), rs.getInt("quantity"), 
                        rs.getString("unit"), String.valueOf(rs.getDate("desired_delivery_date"))));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public ObservableList<ImportRequestHistory> findAllHistory() {
        ObservableList<ImportRequestHistory> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM ycnh_history ORDER BY id DESC";
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ImportRequestHistory(rs.getInt("id"), rs.getString("ycnh_id"), rs.getString("action_type"),
                        rs.getString("changed_by"), rs.getString("diff_text"), rs.getString("reason"), rs.getString("changed_at")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public void deleteImportRequest(String id) throws Exception {
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM ImportRequest WHERE id = ?")) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    public void insertNewRequest(String reqId, String user, List<ImportRequestDetail> detailsList) throws Exception {
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Insert Master
                String sqlMaster = "INSERT INTO ImportRequest (id, created_by) VALUES (?, ?)";
                try(PreparedStatement psM = conn.prepareStatement(sqlMaster)) {
                    psM.setString(1, reqId); psM.setString(2, user); psM.executeUpdate();
                }

                // 2. Insert Details
                String sqlDetail = "INSERT INTO ycnh_chitiet (ycnh_id, merchandise_code, quantity, unit, desired_delivery_date) VALUES (?, ?, ?, ?, ?)";
                try(PreparedStatement psD = conn.prepareStatement(sqlDetail)) {
                    for(ImportRequestDetail ct : detailsList) {
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
            }
        }
    }

    public void updateRequest(String reqId, List<ImportRequestDetail> inserts, List<ImportRequestDetail> updates, List<ImportRequestDetail> deletes, String diffText, String reason, String user) throws Exception {
        try (Connection conn = com.system.infrastructure.persistence.Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete
                try(PreparedStatement psD = conn.prepareStatement("DELETE FROM ycnh_chitiet WHERE id = ?")) {
                    for(ImportRequestDetail c : deletes) { psD.setInt(1, c.getId()); psD.addBatch(); }
                    psD.executeBatch();
                }
                // Update
                try(PreparedStatement psU = conn.prepareStatement("UPDATE ycnh_chitiet SET quantity=?, desired_delivery_date=? WHERE id=?")) {
                    for(ImportRequestDetail c : updates) { 
                        psU.setInt(1, c.getQuantity()); psU.setDate(2, java.sql.Date.valueOf(c.getDesiredDeliveryDate()));
                        psU.setInt(3, c.getId()); psU.addBatch(); 
                    }
                    psU.executeBatch();
                }
                // Add
                try(PreparedStatement psA = conn.prepareStatement("INSERT INTO ycnh_chitiet (ycnh_id, merchandise_code, quantity, unit, desired_delivery_date) VALUES (?, ?, ?, ?, ?)")) {
                    for(ImportRequestDetail c : inserts) {
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
            }
        }
    }
}
