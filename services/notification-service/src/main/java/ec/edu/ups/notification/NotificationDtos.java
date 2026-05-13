package ec.edu.ups.notification;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.List;

public final class NotificationDtos {

    private NotificationDtos() {
    }

    public record Recipient(
            @NotBlank String name,
            @NotBlank @Email String email
    ) {
    }

    public record NotificationRequest(
            Long requestId,
            @NotBlank String topic,
            @NotBlank String subjectName,
            @NotEmpty List<@Valid Recipient> recipients
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
}
