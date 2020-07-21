package no.nav.registre.testnorge.synt.sykemelding.provider;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;

import static no.nav.registre.testnorge.synt.sykemelding.util.TestUtil.getTestArbeidsforholdDTO;
import static no.nav.registre.testnorge.synt.sykemelding.util.TestUtil.getTestHistorikk;
import static no.nav.registre.testnorge.synt.sykemelding.util.TestUtil.getTestLegeListeDTO;
import static no.nav.registre.testnorge.synt.sykemelding.util.TestUtil.getTestPersonDataDTO;
import static no.nav.registre.testnorge.synt.sykemelding.util.TestUtil.getTestOrganisasjonDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.registre.testnorge.dto.synt.sykemelding.v1.SyntSykemeldingDTO;
import no.nav.registre.testnorge.synt.sykemelding.domain.Arbeidsforhold;
import no.nav.registre.testnorge.synt.sykemelding.domain.Lege;
import no.nav.registre.testnorge.synt.sykemelding.domain.Person;
import no.nav.registre.testnorge.synt.sykemelding.domain.Sykemelding;
import no.nav.registre.testnorge.test.JsonWiremockHelper;

import org.junit.jupiter.api.AfterEach;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
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

    @Test
    public void shouldOpprettSyntSykemelding() throws Exception {

        var dto = SyntSykemeldingDTO.builder()
                .arbeidsforholdId(arbeidsforholdId)
                .ident(ident)
                .orgnummer(orgnr)
                .startDato(LocalDate.now())
                .build();

        var hodejegerenUrl = "(.*)/hodejegeren/api/v1/persondata";
        var arbeidsforholdUrl = "(.*)/arbeidsforhold/api/v1/arbeidsforhold/" + ident + "/" + orgnr + "/" + arbeidsforholdId;
        var organisasjonUrl = "(.*)/organisasjon/api/v1/organisasjoner/" + orgnr;
        var historikkUrl = "(.*)/synt/api/v1/generate_sykmeldings_history_json";
        var helsepersonellUrl = "(.*)/helsepersonell/api/v1/helsepersonell/leger";
        var sykemeldingUrl = "(.*)/sykemelding/api/v1/sykemeldinger";

        var hodejegerenResponse = getTestPersonDataDTO(ident);

        var arbeidsforholdResponse = getTestArbeidsforholdDTO(arbeidsforholdId, orgnr);

        var organisasjonResponse = getTestOrganisasjonDTO(orgnr);

        var arbeidsforhold = new Arbeidsforhold(
                arbeidsforholdResponse,
                organisasjonResponse
        );

        var historikkRequest = Map.of(ident, LocalDate.now());
        var historikkResponse = getTestHistorikk(ident);

        var helsepersonellResponse = getTestLegeListeDTO();

        var sykemeldingRequest = new Sykemelding(
                new Person(hodejegerenResponse),
                historikkResponse.get(ident),
                dto,
                new Lege(helsepersonellResponse.getLeger().get(0)),
                arbeidsforhold).toDTO();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hodejegerenUrl)
                .withQueryParam("ident", ident)
                .withQueryParam("miljoe", miljoe)
                .withResponseBody(hodejegerenResponse)
                .stubGet();

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