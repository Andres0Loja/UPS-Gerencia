package ec.edu.ups.academic;

import ec.edu.ups.academic.AcademicDtos.AsignaturaDto;
import ec.edu.ups.academic.AcademicDtos.CreateSolicitudRequest;
import ec.edu.ups.academic.AcademicDtos.EstudianteDto;
import ec.edu.ups.academic.AcademicDtos.ProfesorDto;
import ec.edu.ups.academic.AcademicDtos.SolicitudDto;
import ec.edu.ups.academic.AcademicDtos.ValidateAcademicRequest;
import ec.edu.ups.academic.AcademicDtos.ValidationResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class AcademicController {

    private final AcademicService service;

    public AcademicController(AcademicService service) {
        this.service = service;
    }

    @GetMapping("/profesores")
    public List<ProfesorDto> profesores() {
        return service.listarProfesores();
    }

    @GetMapping("/estudiantes")
    public List<EstudianteDto> estudiantes() {
        return service.listarEstudiantes();
    }

    @GetMapping("/asignaturas")
    public List<AsignaturaDto> asignaturas() {
        return service.listarAsignaturas();
    }

    @GetMapping("/asignaturas/{id}")
    public AsignaturaDto asignatura(@PathVariable Long id) {
        return service.obtenerAsignatura(id);
    }

    @PostMapping("/academic/validate-request")
    public ValidationResponse validar(@Valid @RequestBody ValidateAcademicRequest request) {
        return service.validar(request);
    }

    @GetMapping("/solicitudes")
    public List<SolicitudDto> solicitudes() {
        return service.listarSolicitudes();
    }

    @GetMapping("/solicitudes/{id}")
    public SolicitudDto solicitud(@PathVariable Long id) {
        return service.obtenerSolicitud(id);
    }

    @PostMapping("/solicitudes")
    public SolicitudDto crearSolicitud(@Valid @RequestBody CreateSolicitudRequest request) {
        return service.crearSolicitud(request);
    }
}
