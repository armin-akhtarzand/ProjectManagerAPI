import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.iths.armin.projectmanagerapi.ProjectManagerApiApplication;

@SpringBootTest(classes = ProjectManagerApiApplication.class)
@ActiveProfiles("test")
public class ProjectManagerApiApplicationTests {

    @Test
    public void contextLoads() {
    }


}
