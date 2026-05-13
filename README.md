# UPS Gerencia - Sistema SOA de recursos academicos

Sistema distribuido web bajo arquitectura SOA para gestionar solicitudes de recursos academicos de una asignatura.

## Proceso de negocio

Un profesor selecciona una asignatura, ingresa un tema academico y elige estudiantes destinatarios. El sistema valida la informacion academica, consulta recursos en proveedores externos, simula una notificacion y registra una solicitud final con historial.

## Servicios

| Servicio | Puerto | Responsabilidad |
| --- | ---: | --- |
| frontend | 5173 | Interfaz web para profesores |
| api-gateway | 8080 | Orquesta el proceso completo |
| academic-service | 8081 | Profesores, estudiantes, asignaturas y solicitudes |
| content-service | 8082 | Integracion con NASA y NYTimes |
| notification-service | 8083 | Notificacion simulada con evidencia |
| PostgreSQL | 5432 | Persistencia del servicio academico |

## Proveedores integrados

- `academic-service`: proveedor interno propio.
- NASA API: proveedor externo.
- NYTimes API: proveedor externo.
- `notification-service`: proveedor interno simulado.

## Cumplimiento SOA

El frontend consume solo el `api-gateway`. El gateway orquesta servicios independientes mediante REST/JSON. Cada servicio tiene una responsabilidad clara y contratos HTTP. Las APIs externas no son llamadas desde el navegador, sino desde `content-service`.

## Requisitos

- Java JDK 21.
- Node.js 24 y npm.
- Docker Desktop.
- Git.

No se requiere Maven global. El repositorio incluye Maven Wrapper.

No se requiere PostgreSQL ni MySQL instalado localmente. La base de datos se ejecuta con Docker Compose.

## Configuracion

Copiar el archivo de ejemplo:

```powershell
Copy-Item .env.example .env
```

Las claves son opcionales:

```text
NASA_API_KEY=
NYTIMES_API_KEY=
```

Si quedan vacias, el sistema funciona en modo demo con fallback controlado.

## Ejecutar en Windows

Abrir Docker Desktop antes de levantar la base.

```powershell
docker compose up -d
```

Ejecutar cada backend en una terminal distinta desde la raiz:

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

Ejecutar frontend:

```powershell
cd frontend
npm install
npm run dev
```

El frontend no usa dependencias externas; `npm install` no descarga paquetes obligatorios.

Abrir:

```text
http://localhost:5173
```

## Probar flujo principal

Con todos los servicios ejecutandose:

```powershell
.\tests\curl\flujo-principal.ps1
```

O manualmente:

```powershell
$body = @{
  professorId = 1
  subjectId = 1
  studentIds = @(1,2)
  topic = "galaxias y exploracion espacial"
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/academic-requests" -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 20
```

## Ejecutar pruebas

```powershell
.\mvnw.cmd test
```

## Documentacion

- [Proceso de negocio](docs/proceso-negocio.md)
- [Arquitectura logica](docs/arquitectura-logica.md)
- [Arquitectura fisica](docs/arquitectura-fisica.md)
- [Proveedores de servicios](docs/proveedores-servicios.md)
- [Detalle de implementacion](docs/detalle-implementacion.md)
- [Instalacion, ejecucion y pruebas](docs/instalacion-ejecucion-pruebas.md)
- [OpenAPI](docs/api/openapi.yaml)

## Defensa academica

Para demostrar el cumplimiento:

1. Mostrar la arquitectura logica y fisica en `docs/`.
2. Levantar PostgreSQL con Docker Compose.
3. Ejecutar los cuatro servicios Spring Boot con `.\mvnw.cmd`.
4. Ejecutar el frontend con npm.
5. Crear una solicitud desde la web.
6. Verificar que participan `academic-service`, NASA API, NYTimes API y `notification-service`.
7. Mostrar el historial de solicitudes y la evidencia de notificacion.
8. Explicar que NASA/NYTimes tienen fallback si faltan claves o internet.
