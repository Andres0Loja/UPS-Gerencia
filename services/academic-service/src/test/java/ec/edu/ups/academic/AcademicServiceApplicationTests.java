package ec.edu.ups.academic;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AcademicServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void profesoresEndpointReturnsSeedData() throws Exception {
        mockMvc.perform(get("/profesores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").exists());
    }

    @Test
    void asignaturasEndpointReturnsStudents() throws Exception {
        mockMvc.perform(get("/asignaturas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estudiantes[0].email").exists());
    }
}
