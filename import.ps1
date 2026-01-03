# PowerShell脚本用于导入数据库课程和知识点
$filePath = "./数据库课程--知识点.xlsx"
$url = "http://localhost:8080/ums/api/import/database-course"

Write-Host "开始导入数据库课程和知识点..."
Write-Host "文件路径: $filePath"
Write-Host "API地址: $url"

# 确保文件存在
if (-not (Test-Path -Path $filePath)) {
    Write-Host "错误: 文件不存在!" -ForegroundColor Red
    exit 1
}

# 创建一个边界分隔符
$boundary = [System.Guid]::NewGuid().ToString()
$bodyLines = @(
    "--$boundary",
    "Content-Disposition: form-data; name=\"file\"; filename=\"$(Split-Path -Leaf $filePath)\"",
    "Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    "",
    [System.IO.File]::ReadAllBytes($filePath),
    "--$boundary--",
    ""
)

# 执行文件上传
try {
    $response = Invoke-RestMethod -Uri $url -Method Post -Body $bodyLines -ContentType "multipart/form-data; boundary=$boundary" -UseBasicParsing
    Write-Host "导入成功!" -ForegroundColor Green
    Write-Host "响应: $response" -ForegroundColor Green
} catch {
    Write-Host "导入失败: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails) {
        Write-Host "详细错误: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
    exit 1
}
