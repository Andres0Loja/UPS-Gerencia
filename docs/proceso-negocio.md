# Proceso de negocio

## Nombre

Gestion de solicitud de recursos academicos para una asignatura.

## Actor principal

Profesor. El profesor inicia el flujo desde la aplicacion web, selecciona una asignatura bajo su responsabilidad, define un tema academico y elige estudiantes destinatarios.

## Objetivo

Generar un dossier academico con recursos externos y evidencia de notificacion para apoyar una actividad de clase, taller o investigacion.

## Entradas

- Identificador del profesor.
- Identificador de la asignatura.
- Tema academico.
- Lista de estudiantes destinatarios.

## Flujo paso a paso

1. El profesor abre el frontend web.
2. El frontend consulta al `api-gateway` los profesores y asignaturas disponibles.
3. El profesor selecciona asignatura, tema y estudiantes.
4. El frontend envia la solicitud a `POST /api/academic-requests`.
5. El `api-gateway` valida los datos con `academic-service`.
6. `academic-service` confirma que la asignatura pertenece al profesor y que los estudiantes pertenecen a la asignatura.
7. El `api-gateway` consulta `content-service` para obtener recursos academicos.
8. `content-service` consulta NASA API y NYTimes API, o devuelve recursos demo controlados si faltan claves o conectividad.
9. El `api-gateway` envia los destinatarios a `notification-service`.
10. `notification-service` simula la notificacion y devuelve evidencia.
11. El `api-gateway` registra la solicitud final en `academic-service`.
12. El frontend muestra dossier, proveedores usados, recursos, notificacion e historial.

## Servicios participantes

- `api-gateway`: orquesta el proceso completo.
- `academic-service`: valida y registra informacion academica.
- `content-service`: integra NASA y NYTimes.
- `notification-service`: genera evidencia de notificacion simulada.
- NASA API: proveedor externo de recurso cientifico.
- NYTimes API: proveedor externo de articulos/noticias.

## Salidas

- Solicitud academica registrada.
- Dossier con recurso NASA.
- Dossier con articulos NYTimes.
- Evidencia de notificacion.
- Lista de proveedores participantes.
- Historial consultable.

## Resultado final

El profesor obtiene una solicitud academica completa y trazable, con contenido de proveedores externos, validacion academica interna y evidencia de notificacion a estudiantes.

## Justificacion como proceso de negocio

El flujo tiene actor, entradas, reglas, proveedores, orquestacion, resultado persistido y trazabilidad. No es un CRUD aislado: coordina servicios distintos para producir un resultado util dentro de una actividad academica.
