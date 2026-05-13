# Proveedores de servicios

## academic-service

- Tipo: proveedor interno propio.
- Entrada: profesor, asignatura, tema y estudiantes.
- Salida: validacion academica y solicitud persistida.
- Rol: fuente de verdad academica; valida que la asignatura pertenece al profesor y que los estudiantes pertenecen a la asignatura.
- Naturaleza: servicio desarrollado por el equipo.

## NASA API

- Tipo: proveedor externo cloud/API.
- Entrada: clave `NASA_API_KEY`.
- Salida: recurso astronomico de NASA APOD.
- Rol: aportar recurso cientifico visual para el dossier academico.
- Naturaleza: API externa. Si no hay clave o conectividad, se usa fallback demo.

## NYTimes API

- Tipo: proveedor externo cloud/API.
- Entrada: tema academico y clave `NYTIMES_API_KEY`.
- Salida: articulos relacionados con el tema.
- Rol: aportar noticias o articulos de contexto para el dossier academico.
- Naturaleza: API externa. Si no hay clave o conectividad, se usa fallback demo.

## notification-service

- Tipo: proveedor interno propio simulado.
- Entrada: tema, asignatura y destinatarios.
- Salida: evidencia de notificacion con identificador, fecha, estado y destinatarios.
- Rol: cerrar el flujo de negocio notificando a estudiantes.
- Naturaleza: servicio desarrollado por el equipo; modo simulado para defensa academica sin SMTP.
