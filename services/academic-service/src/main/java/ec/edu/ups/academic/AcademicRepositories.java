package ec.edu.ups.academic;

import org.springframework.data.jpa.repository.JpaRepository;

interface ProfesorRepository extends JpaRepository<Profesor, Long> {
}

interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
}

interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {
}

interface SolicitudAcademicaRepository extends JpaRepository<SolicitudAcademica, Long> {
}
