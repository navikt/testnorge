package no.nav.registre.sdforvalter;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {
    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }
}
