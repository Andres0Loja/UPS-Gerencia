package ec.edu.ups.gateway;

import ec.edu.ups.gateway.GatewayDtos.AcademicProcessResponse;
import ec.edu.ups.gateway.GatewayDtos.AcademicRequest;
import ec.edu.ups.gateway.GatewayDtos.ContentSearchResponse;
import ec.edu.ups.gateway.GatewayDtos.CreateSolicitudRequest;
import ec.edu.ups.gateway.GatewayDtos.NotificationRequest;
import ec.edu.ups.gateway.GatewayDtos.NotificationResponse;
import ec.edu.ups.gateway.GatewayDtos.ProfessorDto;
import ec.edu.ups.gateway.GatewayDtos.Recipient;
import ec.edu.ups.gateway.GatewayDtos.SolicitudDto;
import ec.edu.ups.gateway.GatewayDtos.StudentDto;
import ec.edu.ups.gateway.GatewayDtos.SubjectDto;
import ec.edu.ups.gateway.GatewayDtos.ValidationRequest;
import ec.edu.ups.gateway.GatewayDtos.ValidationResponse;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Service
public class GatewayService {

    private final RestClient restClient;
    private final String academicServiceUrl;
    private final String contentServiceUrl;
    private final String notificationServiceUrl;

    public GatewayService(
            RestClient restClient,
            @Value("${ACADEMIC_SERVICE_URL:http://localhost:8081}") String academicServiceUrl,
            @Value("${CONTENT_SERVICE_URL:http://localhost:8082}") String contentServiceUrl,
            @Value("${NOTIFICATION_SERVICE_URL:http://localhost:8083}") String notificationServiceUrl
    ) {
        this.restClient = restClient;
        this.academicServiceUrl = trimSlash(academicServiceUrl);
        this.contentServiceUrl = trimSlash(contentServiceUrl);
        this.notificationServiceUrl = trimSlash(notificationServiceUrl);
    }

    public List<ProfessorDto> profesores() {
        return getList(academicServiceUrl + "/profesores", new ParameterizedTypeReference<>() {
        });
    }

    public List<StudentDto> estudiantes() {
        return getList(academicServiceUrl + "/estudiantes", new ParameterizedTypeReference<>() {
        });
    }

    public List<SubjectDto> asignaturas() {
        return getList(academicServiceUrl + "/asignaturas", new ParameterizedTypeReference<>() {
        });
    }

    public List<SolicitudDto> solicitudes() {
        return getList(academicServiceUrl + "/solicitudes", new ParameterizedTypeReference<>() {
        });
    }

    public SolicitudDto solicitud(Long id) {
        return get(academicServiceUrl + "/solicitudes/" + id, SolicitudDto.class);
    }

    public AcademicProcessResponse execute(AcademicRequest request) {
        ValidationResponse validation = post(
                academicServiceUrl + "/academic/validate-request",
                new ValidationRequest(request.professorId(), request.subjectId(), request.studentIds(), request.topic()),
                ValidationResponse.class
        );

        ContentSearchResponse content = get(
                contentServiceUrl + "/content/search?topic={topic}",
                ContentSearchResponse.class,
                request.topic()
        );

        NotificationResponse notification = post(
                notificationServiceUrl + "/notifications/send",
                new NotificationRequest(
                        null,
                        request.topic(),
                        validation.subject().nombre(),
                        validation.students().stream()
                                .map(student -> new Recipient(student.nombre(), student.email()))
                                .toList()
                ),
                NotificationResponse.class
        );

        List<String> providers = new ArrayList<>(new LinkedHashSet<>(content.providersUsed()));
        providers.add("academic-service");
        providers.add("notification-service");

        String summary = "Solicitud academica generada para " + validation.students().size()
                + " estudiante(s). Recursos NASA: " + content.nasaResources().size()
                + ". Articulos NYTimes: " + content.nytimesArticles().size()
                + ". Evidencia de notificacion: " + notification.evidenceId() + ".";

        SolicitudDto savedRequest = post(
                academicServiceUrl + "/solicitudes",
                new CreateSolicitudRequest(
                        request.professorId(),
                        request.subjectId(),
                        request.studentIds(),
                        request.topic(),
                        providers,
                        summary,
                        "COMPLETADA"
                ),
                SolicitudDto.class
        );

        return new AcademicProcessResponse(
                savedRequest,
                validation.professor(),
                validation.subject(),
                validation.students(),
                request.topic(),
                content.nasaResources(),
                content.nytimesArticles(),
                notification,
                providers,
                "COMPLETADA",
                content.fallbackMode(),
                content.warnings()
        );
    }

    private <T> List<T> getList(String url, ParameterizedTypeReference<List<T>> type) {
        try {
            return restClient.get().uri(url).retrieve().body(type);
        } catch (RestClientResponseException ex) {
            throw responseException(ex);
        } catch (RestClientException ex) {
            throw unavailable(ex);
        }
    }

    private <T> T get(String url, Class<T> type, Object... uriVariables) {
        try {
            return restClient.get().uri(url, uriVariables).retrieve().body(type);
        } catch (RestClientResponseException ex) {
            throw responseException(ex);
        } catch (RestClientException ex) {
            throw unavailable(ex);
        }
    }

    private <T> T post(String url, Object payload, Class<T> type) {
        try {
            return restClient.post().uri(url).body(payload).retrieve().body(type);
        } catch (RestClientResponseException ex) {
            throw responseException(ex);
        } catch (RestClientException ex) {
            throw unavailable(ex);
        }
    }

    private ResponseStatusException responseException(RestClientResponseException ex) {
        return new ResponseStatusException(
                ex.getStatusCode(),
                "Error de servicio proveedor: " + ex.getResponseBodyAsString(),
                ex
        );
    }

    private ResponseStatusException unavailable(RestClientException ex) {
        return new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "No se pudo comunicar con un servicio proveedor. Verifica que academic, content y notification esten ejecutandose.",
                ex
        );
    }

    private String trimSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
