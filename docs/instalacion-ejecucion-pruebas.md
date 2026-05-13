# Instalacion, ejecucion y pruebas

## Requisitos

- Windows 11.
- Java JDK 21 con `JAVA_HOME`.
- Node.js 24 y npm.
- Git.
- Docker Desktop.

No se requiere Maven global. El proyecto usa Maven Wrapper en la raiz.

No se requiere PostgreSQL ni MySQL local. La base de datos se levanta con Docker Compose.

Antes de ejecutar, abrir Docker Desktop y esperar a que este iniciado.

## Configuracion

Crear `.env` desde el ejemplo:

```powershell
Copy-Item .env.example .env
```

Las claves `NASA_API_KEY` y `NYTIMES_API_KEY` son opcionales. Si quedan vacias, el sistema funciona en modo demo.

## Levantar base de datos

```powershell
docker compose up -d
```

## Ejecutar backends en Windows

Abrir una terminal por servicio desde la raiz del repositorio.

```powershell
.\mvnw.cmd -pl services/academic-service spring-boot:run
```

```powershell
.\mvnw.cmd -pl services/content-service spring-boot:run
```

```powershell
.\mvnw.cmd -pl services/notification-service spring-boot:run
```

```powershell
.\mvnw.cmd -pl services/api-gateway spring-boot:run
```

## Ejecutar frontend

```powershell
cd frontend
npm install
npm run dev
```

El frontend no depende de paquetes externos; `npm install` es opcional, pero se mantiene como comando estandar para la defensa.

Abrir:

```text
http://localhost:5173
```

## Probar el flujo principal con curl

```powershell
.\tests\curl\flujo-principal.ps1
```

Tambien puede probarse manualmente:

```powershell
$body = @{
  professorId = 1
  subjectId = 1
  studentIds = @(1,2)
  topic = "galaxias y exploracion espacial"
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/academic-requests" -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 20
```

## Compilar y ejecutar pruebas Java

```powershell
.\mvnw.cmd test
```

## Evidencia esperada

- La respuesta del gateway tiene estado `COMPLETADA`.
- La respuesta incluye proveedores: `academic-service`, `NASA API`, `NYTimes API`, `notification-service`.
- Si no hay claves externas, los recursos aparecen con `fallback=true`.
- El historial queda disponible en `GET http://localhost:8080/api/solicitudes`.
