package com.system.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DatabaseUrlParsingTest {

    @Test
    void parsesSimpleUrl() {
        Database.DatabaseConfig cfg = Database.parseDatabaseUrl(
                "postgres://postgres:admin@localhost:5432/ITSS");

        assertEquals("jdbc:postgresql://localhost:5432/ITSS", cfg.jdbcUrl());
        assertEquals("postgres", cfg.user());
        assertEquals("admin", cfg.password());
        assertEquals("localhost", cfg.host());
        assertEquals(5432, cfg.port());
        assertEquals("ITSS", cfg.dbName());
    }

    @Test
    void parsesUrlWithSsl() {
        Database.DatabaseConfig cfg = Database.parseDatabaseUrl(
                "postgres://avnadmin:secret123@pg.example.com:15759/defaultdb?sslmode=require");

        assertEquals("jdbc:postgresql://pg.example.com:15759/defaultdb?sslmode=require", cfg.jdbcUrl());
        assertEquals("avnadmin", cfg.user());
        assertEquals("secret123", cfg.password());
        assertEquals("pg.example.com", cfg.host());
        assertEquals(15759, cfg.port());
        assertEquals("defaultdb", cfg.dbName());
    }

    @Test
    void parsesUrlWithDecodedPassword() {
        Database.DatabaseConfig cfg = Database.parseDatabaseUrl(
                "postgres://user:p%40ss@host:5432/db");

        assertEquals("jdbc:postgresql://host:5432/db", cfg.jdbcUrl());
        assertEquals("user", cfg.user());
        assertEquals("p@ss", cfg.password());
    }

    @Test
    void parsesNgrokUrl() {
        Database.DatabaseConfig cfg = Database.parseDatabaseUrl(
                "postgres://postgres:admin@0.tcp.ngrok.io:12345/ITSS");

        assertEquals("jdbc:postgresql://0.tcp.ngrok.io:12345/ITSS", cfg.jdbcUrl());
        assertEquals("postgres", cfg.user());
        assertEquals(12345, cfg.port());
    }

    @Test
    void rejectsInvalidUrl() {
        assertThrows(IllegalArgumentException.class,
                () -> Database.parseDatabaseUrl("not-a-url"));
    }

    @Test
    void rejectsNullUrl() {
        assertThrows(NullPointerException.class,
                () -> Database.parseDatabaseUrl(null));
    }
}
