package no.nav.testnav.apps.tpsmessagingfrontend;


import no.nav.dolly.libs.test.DollyApplicationContextTest;
import no.nav.dolly.libs.test.DollySpringBootTest;

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
