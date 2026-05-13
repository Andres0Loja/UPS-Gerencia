package ec.edu.ups.gateway;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public final class GatewayDtos {

    private GatewayDtos() {
    }

    public record AcademicRequest(
            @NotNull Long professorId,
            @NotNull Long subjectId,
            @NotEmpty List<Long> studentIds,
            @NotBlank String topic
    ) {
    }

    public record ProfessorDto(Long id, String nombre, String email) {
    }

    public record StudentDto(Long id, String nombre, String email) {
    }

    public record SubjectDto(
            Long id,
            String nombre,
            String ciclo,
            ProfessorDto profesor,
            List<StudentDto> estudiantes
    ) {
    }

    public record ValidationRequest(
            Long professorId,
            Long subjectId,
            List<Long> studentIds,
            String topic
    ) {
    }

    public record ValidationResponse(
            boolean valid,
            String message,
            ProfessorDto professor,
            SubjectDto subject,
            List<StudentDto> students
    ) {
    }

    public record CreateSolicitudRequest(
            Long professorId,
            Long subjectId,
            List<Long> studentIds,
            String topic,
            List<String> providersUsed,
            String resultSummary,
            String status
    ) {
    }

    public record SolicitudDto(
            Long id,
            String topic,
            Instant createdAt,
            String status,
            List<String> providersUsed,
            String resultSummary,
            ProfessorDto professor,
            SubjectDto subject,
            List<StudentDto> students
    ) {
    }

    public record ResourceItem(
            String provider,
            String title,
            String description,
            String url,
            boolean fallback,
            String message
    ) {
    }

    public record ContentSearchResponse(
            String topic,
            List<ResourceItem> nasaResources,
            List<ResourceItem> nytimesArticles,
            List<String> providersUsed,
            boolean fallbackMode,
            List<String> warnings
    ) {
    }

    public record Recipient(String name, String email) {
    }

    public record NotificationRequest(
            Long requestId,
            String topic,
            String subjectName,
            List<Recipient> recipients
    ) {
    }

    public record NotificationResponse(
            String evidenceId,
            String status,
            Instant sentAt,
            int recipientsCount,
            List<String> recipients,
            String message
    ) {
    }

    public record AcademicProcessResponse(
            SolicitudDto request,
            ProfessorDto professor,
            SubjectDto subject,
            List<StudentDto> students,
            String topic,
            List<ResourceItem> nasaResources,
            List<ResourceItem> nytimesArticles,
            NotificationResponse notification,
            List<String> providersUsed,
            String finalStatus,
            boolean externalFallbackUsed,
            List<String> warnings
    ) {
    }
}
