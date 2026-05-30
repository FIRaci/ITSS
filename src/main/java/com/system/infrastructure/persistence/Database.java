package com.system.infrastructure.persistence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Database {

    private static final String JDBC_URL;
    private static final String DB_USER;
    private static final String DB_PASSWORD;

    static {
        Map<String, String> envVars = loadEnvFile();

        String databaseUrl = envVars.getOrDefault("DATABASE_URL", null);
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            try {
                String normalized = databaseUrl.replace("postgres://", "postgresql://");
                URI uri = new URI(normalized);

                String host = uri.getHost();
                int port = uri.getPort();
                String dbName = uri.getPath().substring(1);
                String query = uri.getQuery();

                String userInfo = uri.getUserInfo();
                int colonIdx = userInfo.indexOf(':');
                String user = userInfo.substring(0, colonIdx);
                String password = userInfo.substring(colonIdx + 1);

                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
                if (query != null && !query.isEmpty()) {
                    jdbcUrl += "?" + query;
                }

                JDBC_URL = jdbcUrl;
                DB_USER = user;
                DB_PASSWORD = password;

                System.out.println("[Database] Connected to: " + host + ":" + port + "/" + dbName);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse DATABASE_URL: " + databaseUrl, e);
            }
        } else {
            JDBC_URL = "jdbc:postgresql://localhost:5432/ITSS";
            DB_USER = "postgres";
            DB_PASSWORD = "admin";
            System.out.println("[Database] Using local PostgreSQL (default)");
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Properties props = new Properties();
            props.setProperty("user", DB_USER);
            props.setProperty("password", DB_PASSWORD);
            if (JDBC_URL.contains("sslmode=require")) {
                props.setProperty("ssl", "true");
                props.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
            }
            return DriverManager.getConnection(JDBC_URL, props);
        } catch (SQLException e) {
            System.err.println("[Database] Connection failed!");
            System.err.println("  URL: " + JDBC_URL);
            System.err.println("  User: " + DB_USER);
            System.err.println("  Error: " + e.getMessage());
            throw e;
        }
    }

    private static Map<String, String> loadEnvFile() {
        Map<String, String> envMap = new HashMap<>();
        String[] possiblePaths = { ".env", System.getProperty("user.dir") + "/.env" };

        for (String pathStr : possiblePaths) {
            Path path = Paths.get(pathStr);
            if (Files.exists(path)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#"))
                            continue;
                        int eq = line.indexOf('=');
                        if (eq > 0) {
                            String key = line.substring(0, eq).trim();
                            String value = line.substring(eq + 1).trim();
                            if (value.length() >= 2 &&
                                    ((value.startsWith("\"") && value.endsWith("\"")) ||
                                     (value.startsWith("'") && value.endsWith("'")))) {
                                value = value.substring(1, value.length() - 1);
                            }
                            envMap.put(key, value);
                        }
                    }
                    System.out.println("[Database] Loaded .env from: " + path.toAbsolutePath());
                    break;
                } catch (IOException e) {
                    System.err.println("[Database] Warning: could not read .env: " + e.getMessage());
                }
            }
        }
        if (envMap.isEmpty()) {
            System.out.println("[Database] No .env file found, using defaults.");
        }
        return envMap;
    }
}
