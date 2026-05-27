package com.system;

import com.system.infrastructure.persistence.Database;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class DumpSchema {
    public static void main(String[] args) {
        String[] tables = {
            "sites", "users", "site_inventory", "ycnh", "ycnh_chitiet", 
            "international_orders", "cancellation_requests", "discrepancy_reports",
            "discrepancy_items", "order_status_history", "site_transport_log", "ycnh_history"
        };
        try (Connection conn = Database.getConnection(); Statement stmt = conn.createStatement()) {
            for (String table : tables) {
                System.out.println("Table: " + table);
                try (ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + " LIMIT 1")) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int colCount = rsmd.getColumnCount();
                    for (int i = 1; i <= colCount; i++) {
                        System.out.println("  - " + rsmd.getColumnName(i) + " (" + rsmd.getColumnTypeName(i) + ")");
                    }
                } catch (Exception e) {
                    System.out.println("  Error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
