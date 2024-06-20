package no.nav.skattekortservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class ApplicationContextTest {

    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
        assertThat(true).isTrue();
    }
}
