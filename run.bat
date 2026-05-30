@echo off
title RetailApp
chcp 65001 >nul

echo [*] Building RetailApp...
call mvn compile -q
if errorlevel 1 (
    echo [!] Build failed
    pause
    exit /b 1
)

:: Cloud DB — DATABASE_URL from .env (Aiven/Supabase)
echo [*] Using cloud database (DATABASE_URL from .env)

echo [*] Starting RetailApp...
call mvn javafx:run

echo [!] Closed.
pause
