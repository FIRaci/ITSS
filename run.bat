@echo off
title RetailApp
chcp 65001 >nul

echo ^<^.^> Building RetailApp...
call mvn compile -q
if %errorlevel% neq 0 (
    echo [!] Build failed
    pause
    exit /b 1
)

echo ^<^.^> Starting RetailApp...
call mvn javafx:run

echo [!] Closed.
pause
