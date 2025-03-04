package no.nav.registre.sdforvalter.provider.rs;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.sdforvalter.consumer.rs.krr.request.KrrRequest;
import no.nav.registre.sdforvalter.database.model.KrrModel;
import no.nav.registre.sdforvalter.database.repository.KrrRepository;
import no.nav.registre.sdforvalter.domain.Krr;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.testing.JsonWiremockHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DollySpringBootTest
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext
class OrkestreringControllerKrrIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KrrRepository repository;

    @AfterEach
    void cleanUp() {
        reset();
        repository.deleteAll();
    }

    @Test
    void shouldInitiateKrrFromDatabase()
            throws Exception {

        var model = new KrrModel();
        model.setFnr("01010112365");
        repository.save(model);

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v1/kontaktinformasjon")
                .stubPost(HttpStatus.CREATED);

        mockMvc.perform(
                        post("/api/v1/orkestrering/krr")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        var request = new KrrRequest(new Krr(model));
        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v1/kontaktinformasjon")
                .withRequestBody(request, "gyldigFra")
                .verifyPost();

    }

}