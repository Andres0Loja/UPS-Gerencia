$ErrorActionPreference = "Stop"

$baseUrl = "http://localhost:8080"

$body = @{
  professorId = 1
  subjectId = 1
  studentIds = @(1, 2)
  topic = "galaxias y exploracion espacial"
} | ConvertTo-Json

Write-Host "Ejecutando proceso principal en $baseUrl/api/academic-requests"
$response = Invoke-RestMethod -Method Post -Uri "$baseUrl/api/academic-requests" -ContentType "application/json" -Body $body
$response | ConvertTo-Json -Depth 20

Write-Host ""
Write-Host "Historial registrado:"
Invoke-RestMethod -Method Get -Uri "$baseUrl/api/solicitudes" | ConvertTo-Json -Depth 20
