package no.nav.registre.testnorge.hendelse.provider;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.LocalDate;

import no.nav.registre.testnorge.dto.hendelse.v1.HendelseType;
import no.nav.registre.testnorge.dto.hodejegeren.v1.PersondataDTO;
import no.nav.registre.testnorge.hendelse.domain.Hendelse;
import no.nav.registre.testnorge.hendelse.repository.model.HendelseModel;
import no.nav.registre.testnorge.test.JsonWiremockHelper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@DirtiesContext
@TestPropertySource(locations = "classpath:application-test.properties")
class HendelseControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_get_hendelser() throws Exception {

        String personIdent = "12125678903";


        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v1/persondata")
                .withQueryParam("ident", personIdent)
                .withResponseBody(PersondataDTO
                        .builder()
                        .fnr(personIdent)
                        .fornavn("Hans")
                        .etternavn("Hansen")
                        .build())
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v1/hendelser")
                .withResponseBody(new Hendelse(new HendelseModel(21321L, HendelseType.SYKEMELDING_OPPRETTET, Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()), personIdent)))
                .stubGet();

        String json = mvc.perform(get("/api/v1/hendelser")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(json);
    }

    @AfterEach
    void tearDown() {
        WireMock.reset();
    }
}
