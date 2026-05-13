package ec.edu.ups.notification;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sendNotificationReturnsEvidence() throws Exception {
        String payload = """
                {
                  "requestId": 1,
                  "topic": "galaxias",
                  "subjectName": "Arquitectura de Software",
                  "recipients": [
                    {"name": "Maria Torres", "email": "maria.torres@est.ups.edu.ec"}
                  ]
                }
                """;

        mockMvc.perform(post("/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SIMULATED_SENT"))
                .andExpect(jsonPath("$.evidenceId").exists());
    }
}
