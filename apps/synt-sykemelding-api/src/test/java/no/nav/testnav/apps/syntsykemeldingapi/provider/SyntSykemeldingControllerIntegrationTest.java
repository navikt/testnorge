package no.nav.testnav.apps.syntsykemeldingapi.provider;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestArbeidsforholdDTO;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestHistorikk;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestLegeListeDTO;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestOrganisasjonDTO;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestPersonDataDTO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import no.nav.testnav.apps.syntsykemeldingapi.consumer.dto.SyntSykemeldingHistorikkDTO;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Arbeidsforhold;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Helsepersonell;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Person;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Sykemelding;
import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;
import no.nav.testnav.libs.dto.hodejegeren.v1.PersondataDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;
import no.nav.testnav.libs.dto.synt.sykemelding.v1.SyntSykemeldingDTO;
import no.nav.testnav.libs.testing.JsonWiremockHelper;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
public class SyntSykemeldingControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ident = "01019049945";
    private static final String miljoe = "q1";
    private static final String orgnr = "123456789";
    private static final String arbeidsforholdId = "1";

    private static final String hodejegerenUrl = "(.*)/hodejegeren/api/v1/persondata";
    private static final String arbeidsforholdUrl = "(.*)/arbeidsforhold/api/v1/arbeidsforhold/" + ident + "/" + orgnr + "/" + arbeidsforholdId;
    private static final String organisasjonUrl = "(.*)/organisasjon/api/v1/organisasjoner/" + orgnr;
    private static final String historikkUrl = "(.*)/synt/api/v1/generate_sykmeldings_history_json";
    private static final String helsepersonellUrl = "(.*)/testnav-helsepersonell/api/v1/helsepersonell";
    private static final String sykemeldingUrl = "(.*)/sykemelding/api/v1/sykemeldinger";

    private SyntSykemeldingDTO dto;
    private PersondataDTO hodejegerenResponse;
    private ArbeidsforholdDTO arbeidsforholdResponse;
    private OrganisasjonDTO organisasjonResponse;
    private final Map<String, String> historikkRequest = Map.of(ident, LocalDate.now().toString());
    private Map<String, SyntSykemeldingHistorikkDTO> historikkResponse;
    private HelsepersonellListeDTO helsepersonellResponse;
    private SykemeldingDTO sykemeldingRequest;
    private final String tokenResponse = "{\"access_token\": \"dummy\"}";

    @BeforeEach
    public void setUp() {

        dto = SyntSykemeldingDTO.builder()
                .arbeidsforholdId(arbeidsforholdId)
                .ident(ident)
                .orgnummer(orgnr)
                .startDato(LocalDate.now())
                .build();

        hodejegerenResponse = getTestPersonDataDTO(ident);
        arbeidsforholdResponse = getTestArbeidsforholdDTO(arbeidsforholdId, orgnr);
        organisasjonResponse = getTestOrganisasjonDTO(orgnr);

        var arbeidsforhold = new Arbeidsforhold(
                arbeidsforholdResponse,
                organisasjonResponse
        );

        historikkResponse = getTestHistorikk(ident);
        helsepersonellResponse = getTestLegeListeDTO();

        sykemeldingRequest = new Sykemelding(
                new Person(hodejegerenResponse),
                historikkResponse.get(ident),
                dto,
                new Helsepersonell(helsepersonellResponse.getHelsepersonell().get(0)),
                arbeidsforhold).toDTO();
    }

    @Test
    public void shouldOpprettSyntSykemelding() throws Exception {
        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hodejegerenUrl)
                .withQueryParam("ident", ident)
                .withQueryParam("miljoe", miljoe)
                .withResponseBody(hodejegerenResponse)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/token/oauth2/v2.0/token")
                .withResponseBody(tokenResponse)
                .stubPost();


        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arbeidsforholdUrl)
                .withResponseBody(arbeidsforholdResponse)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(organisasjonUrl)
                .withResponseBody(organisasjonResponse)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(historikkUrl)
                .withRequestBody(historikkRequest)
                .withResponseBody(historikkResponse)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(helsepersonellUrl)
                .withResponseBody(helsepersonellResponse)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(sykemeldingUrl)
                .withRequestBody(sykemeldingRequest)
                .stubPost();

        mvc.perform(post("/api/v1/synt-sykemelding")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hodejegerenUrl)
                .withQueryParam("ident", ident)
                .withQueryParam("miljoe", miljoe)
                .withResponseBody(hodejegerenResponse)
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arbeidsforholdUrl)
                .withResponseBody(arbeidsforholdResponse)
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(organisasjonUrl)
                .withResponseBody(organisasjonResponse)
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(historikkUrl)
                .withRequestBody(historikkRequest)
                .withResponseBody(historikkResponse)
                .verifyPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(helsepersonellUrl)
                .withResponseBody(helsepersonellResponse)
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(sykemeldingUrl)
                .withRequestBody(sykemeldingRequest)
                .verifyPost();

    }

    @AfterEach
    public void tearDown() {
        reset();
    }
}