package no.nav.testnav.apps.brukerservice;

import no.nav.testnav.apps.brukerservice.initializer.WireMockInitializer;
import no.nav.dolly.libs.nais.DollySpringBootTest;
import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DollySpringBootTest(initializers = {
        NaisEnvironmentApplicationContextInitializer.class,
        WireMockInitializer.class
})
class ApplicationContextTest {

    @Test
    void load_app_context() {
        assertThat(true).isTrue();
    }

}
