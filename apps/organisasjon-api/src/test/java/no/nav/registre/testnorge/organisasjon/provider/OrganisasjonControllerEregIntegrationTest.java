package no.nav.registre.testnorge.organisasjon.provider;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import no.nav.registre.testnorge.organisasjon.consumer.dto.DetaljerDTO;
import no.nav.registre.testnorge.organisasjon.consumer.dto.NavnDTO;
import no.nav.registre.testnorge.organisasjon.consumer.dto.OrganisasjonDTO;
import no.nav.registre.testnorge.libs.test.JsonWiremockHelper;

import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
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


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class OrganisasjonControllerEregIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String orgnummer = "123456789";
    private static final String miljo = "test";
    private static final String eregUrl = "/ereg-"+miljo+"/api/v1/organisasjon/" + orgnummer;

    private OrganisasjonDTO organisasjonReponse;

    @Before
    public void setUp(){
        organisasjonReponse = OrganisasjonDTO.builder()
                .navn(NavnDTO.builder().navnelinje1("NavneLinje").redigertnavn("RedigertNavn").build())
                .type("Type")
                .detaljer(DetaljerDTO.builder().enhetstype("Enhetstype").build())
                .organisasjonsnummer(orgnummer)
                .build();
    }

    @Test
    public void shouldGetOrganisasjon() throws Exception {

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(eregUrl)
                .withQueryParam("inkluderHierarki", "true")
                .withQueryParam("inkluderHistorikk", "false")
                .withResponseBody(organisasjonReponse)
                .stubGet();

        mvc.perform(get("/api/v1/organisasjoner/" + orgnummer)
                .contentType(MediaType.APPLICATION_JSON)
                .header("miljo", miljo))
                .andExpect(status().isOk());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(eregUrl)
                .withQueryParam("inkluderHierarki", "true")
                .withQueryParam("inkluderHistorikk", "false")
                .withResponseBody(organisasjonReponse)
                .verifyGet();

    }

    @AfterEach
    public void tearDown() {
        reset();
    }
}