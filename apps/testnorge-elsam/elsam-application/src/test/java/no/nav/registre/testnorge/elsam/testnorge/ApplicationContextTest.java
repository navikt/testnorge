package no.nav.registre.testnorge.elsam.testnorge;


import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ApplicationContextTest {

    /**
     * //TODO setup test config
     * Used to test app runtime dependency and properties
     */
    @Test
    @Ignore
    public void load_app_context() {
    }
}
