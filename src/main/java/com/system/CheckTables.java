package com.system;

import com.system.infrastructure.persistence.Database;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class CheckTables {
    public static void main(String[] args) {
        try (Connection conn = Database.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null, "public", "%", new String[] {"TABLE"});
            System.out.println("Danh sách các bảng trong DB:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("TABLE_NAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
