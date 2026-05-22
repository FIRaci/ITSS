$files = Get-ChildItem -Path "c:\Users\TSC\Desktop\Nothing\Another ITSS\Code\retailapp\src\main\java\com\itss" -Filter "*.java" -Recurse
$utf8NoBom = New-Object System.Text.UTF8Encoding $False

foreach ($f in $files) {
    # Read raw bytes and decode properly to preserve Vietnamese
    $bytes = [System.IO.File]::ReadAllBytes($f.FullName)
    # Check if has BOM (EF BB BF)
    if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
        $text = [System.Text.Encoding]::UTF8.GetString($bytes, 3, $bytes.Length - 3)
        [System.IO.File]::WriteAllText($f.FullName, $text, $utf8NoBom)
    } else {
        # Might just be weird encoding from previous run, let's ensure it's saved without BOM
        $text = [System.IO.File]::ReadAllText($f.FullName)
        [System.IO.File]::WriteAllText($f.FullName, $text, $utf8NoBom)
    }
}
Write-Host "BOM removed!"
