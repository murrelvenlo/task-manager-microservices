package fact.it.userservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
class UserServiceApplicationTests {

    private ObjectMapper mapper;
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

}
