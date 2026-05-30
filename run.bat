@echo off
echo ==============================================
echo        Starting RetailApp
echo ==============================================

echo Building the project...
call mvn compile
if %errorlevel% neq 0 (
    echo [ERROR] Build failed! Please check the errors above.
    pause
    exit /b %errorlevel%
)

echo.
echo Running the application...
call mvn javafx:run
if %errorlevel% neq 0 (
    echo [ERROR] Application exited with an error.
    pause
    exit /b %errorlevel%
)

echo.
echo Application closed successfully.
pause
