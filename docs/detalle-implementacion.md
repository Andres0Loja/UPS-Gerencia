# Detalle de implementacion

## Tecnologias usadas

- Java 21.
- Spring Boot 3.3.5.
- Spring Web.
- Spring Data JPA.
- PostgreSQL en Docker Compose.
- Maven Wrapper en la raiz.
- Frontend estatico con JavaScript, HTML, CSS y servidor Node.js nativo.
- REST, JSON y HTTP.

## Justificacion de Java 21

Java 21 coincide con el entorno disponible, es una version LTS y permite ejecutar Spring Boot 3 sin instalar runtimes adicionales.

## Justificacion de Spring Boot 3

Spring Boot 3 reduce configuracion manual, permite levantar servicios REST rapidamente y facilita pruebas con JUnit. Es mas simple y reproducible que mantener WARs antiguos en WildFly.

## Justificacion de REST/JSON

REST/JSON es suficiente para demostrar SOA en un proyecto academico, facilita pruebas con curl/Postman y evita clientes SOAP generados con rutas locales temporales.

## Justificacion de Docker Compose

La base de datos se ejecuta en Docker para no depender de PostgreSQL instalado localmente. Docker Compose deja el entorno reproducible.

## Estructura del proyecto

```text
services/
  api-gateway/
  academic-service/
  content-service/
  notification-service/
frontend/
docs/
database/
tests/
```

## Endpoints principales

- `POST /api/academic-requests`: ejecuta el proceso completo desde el gateway.
- `GET /api/profesores`: proxy de profesores.
- `GET /api/asignaturas`: proxy de asignaturas.
- `GET /api/solicitudes`: historial.
- `POST /academic/validate-request`: validacion academica interna.
- `GET /content/search?topic=...`: integracion NASA/NYTimes.
- `POST /notifications/send`: notificacion simulada.

## Flujo de ejecucion

1. Frontend envia solicitud al gateway.
2. Gateway valida con `academic-service`.
3. Gateway consulta `content-service`.
4. `content-service` consulta NASA y NYTimes o devuelve fallback.
5. Gateway llama `notification-service`.
6. Gateway registra resultado en `academic-service`.
7. Gateway responde al frontend con el dossier consolidado.

## Manejo de errores

- Validaciones de negocio devuelven HTTP 400.
- Recursos no encontrados devuelven HTTP 404.
- Fallos de comunicacion entre servicios devuelven HTTP 502 desde el gateway.
- Fallos de NASA/NYTimes no rompen el flujo; se activa fallback demo.

## Variables de entorno

Las variables estan documentadas en `.env.example`. Las claves externas son:

- `NASA_API_KEY`
- `NYTIMES_API_KEY`

No hay claves hardcodeadas en el codigo fuente.

## Fallback de APIs externas

`content-service` revisa si existen claves. Si faltan, devuelve recursos demo marcados con `fallback=true`. Si la API externa falla o no hay internet, captura el error y devuelve una respuesta controlada con `warnings`.
