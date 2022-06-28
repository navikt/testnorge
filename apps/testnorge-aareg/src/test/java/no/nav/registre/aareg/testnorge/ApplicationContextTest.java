package no.nav.registre.aareg.testnorge;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class ApplicationContextTest {
    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }
}

