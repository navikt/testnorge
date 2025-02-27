package no.nav.testnav.apps.endringsmeldingfrontend;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.libs.test.DollyApplicationContextTest;

@DollySpringBootTest
class ApplicationContextTest extends DollyApplicationContextTest {

    @Override
    public void testNonexistingApiEndpoint() {
        webTestClient
                .get()
                .uri("/api/someNonExistingEndpoint")
                .exchange()
                .expectStatus()
                .is3xxRedirection();
    }

}
