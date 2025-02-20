package no.nav.testnav.apps.brukerservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.libs.test.DollyApplicationContextTest;

@DollySpringBootTest(initializers = {
        NaisEnvironmentApplicationContextInitializer.class,
        WireMockInitializer.class
})
class ApplicationContextTest extends DollyApplicationContextTest {
}
