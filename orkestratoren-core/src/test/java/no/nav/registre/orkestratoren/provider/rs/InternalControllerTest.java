package no.nav.registre.orkestratoren.provider.rs;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

@RunWith(SpringRunner.class)
@RestClientTest(InternalController.class)
@TestPropertySource(locations = "classpath:unittest/provider/internalController.properties")
public class InternalControllerTest {

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private InternalController controller;

    /**
     * Hvis minst en av orkestratorens avhengigheter ikke er klare til å motta kall (returnerer ikke 200 status for isReady), så
     * skal orkestratorens respons på isReady være status 500.
     */
    @Test
    public void shouldReturnNotReady() {
        this.server.expect(requestTo("https://dummyUrl.skd/internal/isReady")).andRespond(withServerError());
        this.server.expect(requestTo("https://dummyUrl.inntekt/internal/isReady")).andRespond(withSuccess());
        this.server.expect(requestTo("https://dummyUrl.aareg/internal/isReady")).andRespond(withServerError());
        this.server.expect(requestTo("https://dummyUrl.eia/internal/isReady")).andRespond(withSuccess());
        this.server.expect(requestTo("https://dummyUrl.sigrun/internal/isReady")).andRespond(withSuccess());
        this.server.expect(requestTo("https://dummyUrl.inst/internal/isReady")).andRespond(withSuccess());

        ResponseEntity<?> ready = controller.isReady();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ready.getStatusCode());
        this.server.verify();
    }

    /**
     * Hvis alle av orkestratorens avhengigheter er klare til å motta kall og returnerer httpstatus OK fra isReady, så skal
     * orkestratorens isReady-endepunkt returnere http-status OK.
     */
    @Test
    public void shouldReturnReady() {
        this.server.expect(requestTo("https://dummyUrl.skd/internal/isReady")).andRespond(withSuccess());
        this.server.expect(requestTo("https://dummyUrl.inntekt/internal/isReady")).andRespond(withSuccess());
        this.server.expect(requestTo("https://dummyUrl.aareg/internal/isReady")).andRespond(withSuccess());
        this.server.expect(requestTo("https://dummyUrl.eia/internal/isReady")).andRespond(withSuccess());
        this.server.expect(requestTo("https://dummyUrl.sigrun/internal/isReady")).andRespond(withSuccess());
        this.server.expect(requestTo("https://dummyUrl.inst/internal/isReady")).andRespond(withSuccess());

        ResponseEntity<?> ready = controller.isReady();

        assertEquals(HttpStatus.OK, ready.getStatusCode());
        this.server.verify();
    }
}