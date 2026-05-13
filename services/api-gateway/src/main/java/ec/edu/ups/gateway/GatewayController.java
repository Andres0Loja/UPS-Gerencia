package ec.edu.ups.gateway;

import ec.edu.ups.gateway.GatewayDtos.AcademicProcessResponse;
import ec.edu.ups.gateway.GatewayDtos.AcademicRequest;
import ec.edu.ups.gateway.GatewayDtos.ProfessorDto;
import ec.edu.ups.gateway.GatewayDtos.SolicitudDto;
import ec.edu.ups.gateway.GatewayDtos.StudentDto;
import ec.edu.ups.gateway.GatewayDtos.SubjectDto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class GatewayController {

    private final GatewayService service;

    public GatewayController(GatewayService service) {
        this.service = service;
    }

    @GetMapping("/profesores")
    public List<ProfessorDto> profesores() {
        return service.profesores();
    }

    @GetMapping("/estudiantes")
    public List<StudentDto> estudiantes() {
        return service.estudiantes();
    }

    @GetMapping("/asignaturas")
    public List<SubjectDto> asignaturas() {
        return service.asignaturas();
    }

    @GetMapping("/solicitudes")
    public List<SolicitudDto> solicitudes() {
        return service.solicitudes();
    }

    @GetMapping("/solicitudes/{id}")
    public SolicitudDto solicitud(@PathVariable Long id) {
        return service.solicitud(id);
    }

    @PostMapping("/academic-requests")
    public AcademicProcessResponse createAcademicRequest(@Valid @RequestBody AcademicRequest request) {
        return service.execute(request);
    }
}
