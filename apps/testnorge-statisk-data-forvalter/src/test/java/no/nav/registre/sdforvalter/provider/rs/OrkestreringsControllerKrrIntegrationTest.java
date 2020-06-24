package no.nav.registre.sdforvalter.provider.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import no.nav.registre.sdforvalter.consumer.rs.request.KrrRequest;
import no.nav.registre.sdforvalter.database.model.KrrModel;
import no.nav.registre.sdforvalter.database.repository.KrrRepository;
import no.nav.registre.sdforvalter.domain.Krr;
import no.nav.registre.testnorge.test.JsonWiremockHelper;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class OrkestreringsControllerKrrIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KrrRepository repository;

    @Test
    public void shouldInitiateKrrFromDatabase() throws Exception {
        KrrModel model = createKrr("01010112365");
        repository.save(model);

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v1/kontaktinformasjon")
                .stubPost();


        mvc.perform(post("/api/v1/orkestrering/krr/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        KrrRequest request = createKrrRequest(model);

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v1/kontaktinformasjon")
                .withRequestBody(request, "gyldigFra")
                .verifyPost();
    }

    private KrrModel createKrr(String fnr) {
        KrrModel model = new KrrModel();
        model.setFnr(fnr);
        return model;
    }

    private KrrRequest createKrrRequest(KrrModel model) {
        return new KrrRequest(new Krr(model));
    }

    @After
    public void cleanUp() {
        reset();
        repository.deleteAll();
    }
}