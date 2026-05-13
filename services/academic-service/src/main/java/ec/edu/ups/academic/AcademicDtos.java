package ec.edu.ups.academic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public final class AcademicDtos {

    private AcademicDtos() {
    }

    public record ProfesorDto(Long id, String nombre, String email) {
    }

    public record EstudianteDto(Long id, String nombre, String email) {
    }

    public record AsignaturaDto(
            Long id,
            String nombre,
            String ciclo,
            ProfesorDto profesor,
            List<EstudianteDto> estudiantes
    ) {
    }

    public record ValidateAcademicRequest(
            @NotNull Long professorId,
            @NotNull Long subjectId,
            @NotEmpty List<Long> studentIds,
            @NotBlank String topic
    ) {
    }

    public record CreateSolicitudRequest(
            @NotNull Long professorId,
            @NotNull Long subjectId,
            @NotEmpty List<Long> studentIds,
            @NotBlank String topic,
            @NotEmpty List<String> providersUsed,
            @NotBlank String resultSummary,
            @NotBlank String status
    ) {
    }

    public record ValidationResponse(
            boolean valid,
            String message,
            ProfesorDto professor,
            AsignaturaDto subject,
            List<EstudianteDto> students
    ) {
    }

    public record SolicitudDto(
            Long id,
            String topic,
            Instant createdAt,
            String status,
            List<String> providersUsed,
            String resultSummary,
            ProfesorDto professor,
            AsignaturaDto subject,
            List<EstudianteDto> students
    ) {
    }
}
