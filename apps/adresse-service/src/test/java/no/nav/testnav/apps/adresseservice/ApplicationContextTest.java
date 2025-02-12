package no.nav.testnav.apps.adresseservice;

import no.nav.testnav.libs.servletcore.config.NaisEnvironmentApplicationContextInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = NaisEnvironmentApplicationContextInitializer.class)
class ApplicationContextTest {

    @Test
    void load_app_context() {
        assertThat(true).isTrue();
    }

}
