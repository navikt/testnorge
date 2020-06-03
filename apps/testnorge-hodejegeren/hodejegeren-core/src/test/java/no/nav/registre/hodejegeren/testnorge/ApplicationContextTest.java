package no.nav.registre.hodejegeren.testnorge;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.registre.hodejegeren.config.ApplicationConfig;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ApplicationConfig.class)
public class ApplicationContextTest {

    /**
     * Used to test app runtime dependency and properties
     */
    @Test
    public void load_app_context() {
    }
}
