package no.nav.testnav.apps.brukerservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import no.nav.testnav.apps.brukerservice.initializer.WireMockInitializer;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = {WireMockInitializer.class})
class ApplicationContextTest {
    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }
}
