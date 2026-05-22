package com.itss;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // You should configure these based on your local Postgres setup
    private static final String URL = "jdbc:postgresql://localhost:5432/ITSS";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin"; // Change this if your postgres password is different

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Database connection failed. Please ensure PostgreSQL is running and credentials are correct.");
            throw e;
        }
    }
}
