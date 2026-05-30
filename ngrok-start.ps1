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

# Load NGROK_AUTHTOKEN from .env (nếu có)
$envFile = ".env"
if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        if ($_ -match '^\s*NGROK_AUTHTOKEN\s*=\s*"(.+)"\s*$') {
            $env:NGROK_AUTHTOKEN = $Matches[1]
            Write-Host "[*] Loaded NGROK_AUTHTOKEN from .env" -ForegroundColor Cyan
        }
    }
}

Write-Host "[*] Starting ngrok TCP tunnel on port 5432..." -ForegroundColor Cyan
Write-Host ""
Write-Host "[*] Sau khi chạy, copy URL dạng 0.tcp.ngrok.io:xxxxx" -ForegroundColor Cyan
Write-Host "    rồi set vào .env:" -ForegroundColor Cyan
Write-Host "    DATABASE_URL=""postgres://postgres:admin@0.tcp.ngrok.io:xxxxx/ITSS""" -ForegroundColor Green
Write-Host ""

& $ngrokPath tcp 5432 --log=stdout
