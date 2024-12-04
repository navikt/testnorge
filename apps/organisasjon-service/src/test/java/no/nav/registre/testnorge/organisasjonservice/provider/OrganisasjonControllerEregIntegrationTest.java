package no.nav.registre.testnorge.organisasjonservice.provider;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import no.nav.registre.testnorge.organisasjonservice.consumer.dto.DetaljerDTO;
import no.nav.registre.testnorge.organisasjonservice.consumer.dto.NavnDTO;
import no.nav.registre.testnorge.organisasjonservice.consumer.dto.OrganisasjonDTO;
import no.nav.testnav.libs.testing.JsonWiremockHelper;

import org.junit.Before;
import org.junit.Ignore;
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
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "classpath:application-test.yml")
public class OrganisasjonControllerEregIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ORGNUMMER = "123456789";
    private static final String MILJO = "test";
    private static final String EREG_URL = "/api/{miljo}/v1/organisasjon/" + ORGNUMMER;

    private OrganisasjonDTO organisasjonReponse;

    @Before
    public void setUp(){
        organisasjonReponse = OrganisasjonDTO.builder()
                .navn(NavnDTO.builder().navnelinje1("NavneLinje").redigertnavn("RedigertNavn").build())
                .type("Type")
                .detaljer(DetaljerDTO.builder().enhetstype("Enhetstype").build())
                .organisasjonsnummer(ORGNUMMER)
                .build();
    }

    @Test
    @Ignore
    public void shouldGetOrganisasjon() throws Exception {

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(EREG_URL)
                .withQueryParam("inkluderHierarki", "true")
                .withQueryParam("inkluderHistorikk", "false")
                .withResponseBody(organisasjonReponse)
                .stubGet();


        mvc.perform(get("/api/v1/organisasjoner/" + ORGNUMMER)
                .contentType(MediaType.APPLICATION_JSON)
                .header("miljo", MILJO));

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(EREG_URL)
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