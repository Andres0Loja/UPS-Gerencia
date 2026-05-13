package ec.edu.ups.gateway;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ec.edu.ups.gateway.GatewayDtos.AcademicProcessResponse;
import ec.edu.ups.gateway.GatewayDtos.NotificationResponse;
import ec.edu.ups.gateway.GatewayDtos.ProfessorDto;
import ec.edu.ups.gateway.GatewayDtos.SolicitudDto;
import ec.edu.ups.gateway.GatewayDtos.StudentDto;
import ec.edu.ups.gateway.GatewayDtos.SubjectDto;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GatewayController.class)
class ApiGatewayApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GatewayService gatewayService;

    @Test
    void academicRequestEndpointReturnsConsolidatedResponse() throws Exception {
        ProfessorDto professor = new ProfessorDto(1L, "Fanny Gutama", "fanny.gutama@ups.edu.ec");
        StudentDto student = new StudentDto(1L, "Maria Torres", "maria.torres@est.ups.edu.ec");
        SubjectDto subject = new SubjectDto(1L, "Arquitectura de Software", "Septimo ciclo", professor, List.of(student));
        SolicitudDto request = new SolicitudDto(
                10L,
                "galaxias",
                Instant.now(),
                "COMPLETADA",
                List.of("academic-service", "NASA API", "NYTimes API", "notification-service"),
                "Solicitud generada",
                professor,
                subject,
                List.of(student)
        );
        AcademicProcessResponse response = new AcademicProcessResponse(
                request,
                professor,
                subject,
                List.of(student),
                "galaxias",
                List.of(),
                List.of(),
                new NotificationResponse("NOTIF-1", "SIMULATED_SENT", Instant.now(), 1, List.of(student.email()), "ok"),
                request.providersUsed(),
                "COMPLETADA",
                true,
                List.of("fallback demo")
        );

        when(gatewayService.execute(any())).thenReturn(response);

        String payload = """
                {
                  "professorId": 1,
                  "subjectId": 1,
                  "studentIds": [1],
                  "topic": "galaxias"
                }
                """;

        mockMvc.perform(post("/api/academic-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.request.id").value(10))
                .andExpect(jsonPath("$.finalStatus").value("COMPLETADA"));
    }
}
