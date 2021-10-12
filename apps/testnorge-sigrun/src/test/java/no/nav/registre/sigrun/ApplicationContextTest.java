package no.nav.registre.sigrun;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationContextTest {

    /**
     * Used to test app runtime dependency and properties
     */
    @Test
    public void load_app_context() {
    }
}
