-- Referencia documental del esquema principal.
-- Spring Data JPA crea o actualiza las tablas automaticamente en modo demo.
-- Este archivo sirve para explicar el modelo en la defensa academica.

CREATE TABLE profesor (
  id BIGSERIAL PRIMARY KEY,
  nombre VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL UNIQUE
);

CREATE TABLE estudiante (
  id BIGSERIAL PRIMARY KEY,
  nombre VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL UNIQUE
);

CREATE TABLE asignatura (
  id BIGSERIAL PRIMARY KEY,
  nombre VARCHAR(160) NOT NULL,
  ciclo VARCHAR(80) NOT NULL,
  profesor_id BIGINT NOT NULL REFERENCES profesor(id)
);

CREATE TABLE solicitud_academica (
  id BIGSERIAL PRIMARY KEY,
  tema VARCHAR(200) NOT NULL,
  fecha_creacion TIMESTAMP NOT NULL,
  estado VARCHAR(40) NOT NULL,
  proveedores_usados TEXT NOT NULL,
  resumen_resultado TEXT NOT NULL,
  profesor_id BIGINT NOT NULL REFERENCES profesor(id),
  asignatura_id BIGINT NOT NULL REFERENCES asignatura(id)
);
