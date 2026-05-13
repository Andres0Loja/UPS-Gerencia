package ec.edu.ups.academic;

import ec.edu.ups.academic.AcademicDtos.AsignaturaDto;
import ec.edu.ups.academic.AcademicDtos.CreateSolicitudRequest;
import ec.edu.ups.academic.AcademicDtos.EstudianteDto;
import ec.edu.ups.academic.AcademicDtos.ProfesorDto;
import ec.edu.ups.academic.AcademicDtos.SolicitudDto;
import ec.edu.ups.academic.AcademicDtos.ValidateAcademicRequest;
import ec.edu.ups.academic.AcademicDtos.ValidationResponse;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AcademicService {

    private final ProfesorRepository profesores;
    private final EstudianteRepository estudiantes;
    private final AsignaturaRepository asignaturas;
    private final SolicitudAcademicaRepository solicitudes;

    public AcademicService(
            ProfesorRepository profesores,
            EstudianteRepository estudiantes,
            AsignaturaRepository asignaturas,
            SolicitudAcademicaRepository solicitudes
    ) {
        this.profesores = profesores;
        this.estudiantes = estudiantes;
        this.asignaturas = asignaturas;
        this.solicitudes = solicitudes;
    }

    public List<ProfesorDto> listarProfesores() {
        return profesores.findAll().stream().map(this::toDto).toList();
    }

    public List<EstudianteDto> listarEstudiantes() {
        return estudiantes.findAll().stream().map(this::toDto).toList();
    }

    public List<AsignaturaDto> listarAsignaturas() {
        return asignaturas.findAll().stream().map(this::toDto).toList();
    }

    public AsignaturaDto obtenerAsignatura(Long id) {
        return toDto(loadAsignatura(id));
    }

    public List<SolicitudDto> listarSolicitudes() {
        return solicitudes.findAll().stream().map(this::toDto).toList();
    }

    public SolicitudDto obtenerSolicitud(Long id) {
        return toDto(loadSolicitud(id));
    }

    public ValidationResponse validar(ValidateAcademicRequest request) {
        Profesor profesor = loadProfesor(request.professorId());
        Asignatura asignatura = loadAsignatura(request.subjectId());
        Set<Estudiante> selectedStudents = loadStudents(request.studentIds());

        validateTopic(request.topic());
        validateAcademicSelection(profesor, asignatura, selectedStudents);

        return new ValidationResponse(
                true,
                "Informacion academica valida para generar la solicitud.",
                toDto(profesor),
                toDto(asignatura),
                selectedStudents.stream().map(this::toDto).toList()
        );
    }

    public SolicitudDto crearSolicitud(CreateSolicitudRequest request) {
        Profesor profesor = loadProfesor(request.professorId());
        Asignatura asignatura = loadAsignatura(request.subjectId());
        Set<Estudiante> selectedStudents = loadStudents(request.studentIds());

        validateTopic(request.topic());
        validateAcademicSelection(profesor, asignatura, selectedStudents);

        SolicitudAcademica solicitud = new SolicitudAcademica(
                request.topic().trim(),
                request.status().trim(),
                String.join(",", request.providersUsed()),
                request.resultSummary().trim(),
                profesor,
                asignatura,
                selectedStudents
        );

        return toDto(solicitudes.save(solicitud));
    }

    private void validateTopic(String topic) {
        if (topic == null || topic.trim().length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tema academico debe tener al menos 3 caracteres.");
        }
    }

    private void validateAcademicSelection(Profesor profesor, Asignatura asignatura, Set<Estudiante> selectedStudents) {
        if (!asignatura.getProfesor().getId().equals(profesor.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La asignatura no pertenece al profesor seleccionado.");
        }

        Set<Long> subjectStudentIds = asignatura.getEstudiantes().stream()
                .map(Estudiante::getId)
                .collect(java.util.stream.Collectors.toSet());

        List<Long> invalidStudents = selectedStudents.stream()
                .map(Estudiante::getId)
                .filter(id -> !subjectStudentIds.contains(id))
                .toList();

        if (!invalidStudents.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hay estudiantes que no pertenecen a la asignatura: " + invalidStudents);
        }
    }

    private Profesor loadProfesor(Long id) {
        return profesores.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado: " + id));
    }

    private Asignatura loadAsignatura(Long id) {
        return asignaturas.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asignatura no encontrada: " + id));
    }

    private SolicitudAcademica loadSolicitud(Long id) {
        return solicitudes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada: " + id));
    }

    private Set<Estudiante> loadStudents(List<Long> ids) {
        Set<Estudiante> selected = new LinkedHashSet<>();
        for (Long id : ids) {
            selected.add(estudiantes.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado: " + id)));
        }
        return selected;
    }

    private ProfesorDto toDto(Profesor profesor) {
        return new ProfesorDto(profesor.getId(), profesor.getNombre(), profesor.getEmail());
    }

    private EstudianteDto toDto(Estudiante estudiante) {
        return new EstudianteDto(estudiante.getId(), estudiante.getNombre(), estudiante.getEmail());
    }

    private AsignaturaDto toDto(Asignatura asignatura) {
        return new AsignaturaDto(
                asignatura.getId(),
                asignatura.getNombre(),
                asignatura.getCiclo(),
                toDto(asignatura.getProfesor()),
                asignatura.getEstudiantes().stream().map(this::toDto).toList()
        );
    }

    private SolicitudDto toDto(SolicitudAcademica solicitud) {
        List<String> providers = Arrays.stream(solicitud.getProveedoresUsados().split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();

        return new SolicitudDto(
                solicitud.getId(),
                solicitud.getTema(),
                solicitud.getFechaCreacion(),
                solicitud.getEstado(),
                providers,
                solicitud.getResumenResultado(),
                toDto(solicitud.getProfesor()),
                toDto(solicitud.getAsignatura()),
                solicitud.getEstudiantes().stream().map(this::toDto).toList()
        );
    }
}
