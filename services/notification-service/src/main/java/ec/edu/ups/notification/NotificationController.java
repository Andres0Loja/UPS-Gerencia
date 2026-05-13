package ec.edu.ups.notification;

import ec.edu.ups.notification.NotificationDtos.NotificationRequest;
import ec.edu.ups.notification.NotificationDtos.NotificationResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping("/notifications/send")
    public NotificationResponse send(@Valid @RequestBody NotificationRequest request) {
        return service.send(request);
    }

    @GetMapping("/notifications")
    public List<NotificationResponse> history() {
        return service.history();
    }
}
