$files = Get-ChildItem -Path "c:\Users\TSC\Desktop\Nothing\Another ITSS\Code\retailapp\src\main\java\com\itss" -Filter "*.java" -Recurse

foreach ($f in $files) {
    $content = Get-Content $f.FullName -Raw

    # Model Class names
    $content = $content -replace '\bYcnhChiTiet\b', 'ImportRequestDetail'
    $content = $content -replace '\bYcnhHistory\b', 'ImportRequestHistory'
    $content = $content -replace '\bYcnh\b', 'ImportRequest'

    # Model Variable names
    $content = $content -replace '\bycnhId\b', 'requestId'
    $content = $content -replace '\bmaHang\b', 'merchandiseCode'
    $content = $content -replace '\bsoLuong\b', 'quantity'
    $content = $content -replace '\bdonVi\b', 'unit'
    $content = $content -replace '\bngayNhan\b', 'desiredDeliveryDate'

    # Method names
    $content = $content -replace '\bshowYcnhList\b', 'showImportRequestList'
    $content = $content -replace '\bsetupYcnhTable\b', 'setupRequestTable'
    $content = $content -replace '\bfetchYcnhMaster\b', 'fetchRequests'
    $content = $content -replace '\bshowYcnhManagement\b', 'showRequestManagement'
    $content = $content -replace '\bprocessYcnh\b', 'processRequest'

    Set-Content -Path $f.FullName -Value $content -Encoding UTF8
}
Write-Host "Refactoring done!"
