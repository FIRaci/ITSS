# RetailApp — Start ngrok tunnel for PostgreSQL
# Yêu cầu: ngrok installed (https://ngrok.com/download)
# Chạy:    .\ngrok-start.ps1

$ngrokPath = "ngrok"
if (-not (Get-Command $ngrokPath -ErrorAction SilentlyContinue)) {
    $paths = @(
        "C:\tools\ngrok.exe",
        "C:\Program Files\ngrok\ngrok.exe",
        "$env:LOCALAPPDATA\ngrok\ngrok.exe",
        "$env:USERPROFILE\ngrok.exe"
    )
    foreach ($p in $paths) {
        if (Test-Path $p) { $ngrokPath = $p; break }
    }
}

if (-not (Get-Command $ngrokPath -ErrorAction SilentlyContinue)) {
    Write-Host "[!] ngrok not found. Download from https://ngrok.com/download" -ForegroundColor Red
    Write-Host "    Extract and place ngrok.exe in PATH or C:\tools\ngrok.exe" -ForegroundColor Yellow
    exit 1
}

Write-Host "[*] Starting ngrok TCP tunnel on port 5432..." -ForegroundColor Cyan
Write-Host "[*] After startup, copy the forwarding URL (e.g. 0.tcp.ngrok.io:12345)" -ForegroundColor Cyan
Write-Host "[*] Then set it in .env as:" -ForegroundColor Cyan
Write-Host "    DATABASE_URL=""postgres://postgres:admin@<ngrok-url>/ITSS""" -ForegroundColor Green
Write-Host ""

& $ngrokPath tcp 5432 --log=stdout
