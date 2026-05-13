package ec.edu.ups.notification;

import ec.edu.ups.notification.NotificationDtos.NotificationRequest;
import ec.edu.ups.notification.NotificationDtos.NotificationResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final List<NotificationResponse> history = new ArrayList<>();

    public synchronized NotificationResponse send(NotificationRequest request) {
        List<String> recipients = request.recipients().stream()
                .map(recipient -> recipient.name() + " <" + recipient.email() + ">")
                .toList();

        NotificationResponse response = new NotificationResponse(
                "NOTIF-" + UUID.randomUUID(),
                "SIMULATED_SENT",
                Instant.now(),
                recipients.size(),
                recipients,
                "Notificacion simulada enviada para la asignatura " + request.subjectName()
                        + " sobre el tema " + request.topic()
        );

        history.add(response);
        return response;
    }

    public synchronized List<NotificationResponse> history() {
        return List.copyOf(history);
    }
}
