package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.dto.SyntSykemeldingHistorikkDTO;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Arbeidsforhold;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Helsepersonell;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Person;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Sykemelding;
import no.nav.testnav.apps.syntsykemeldingapi.domain.pdl.PdlPerson;
import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;
import no.nav.testnav.libs.dto.synt.sykemelding.v1.SyntSykemeldingDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestArbeidsforholdDTO;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestHistorikk;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestLegeListeDTO;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestOrganisasjonDTO;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestPdlPerson;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureWireMock(port = 0)
public class SykemeldingConsumerTest {

    @MockitoBean
    @SuppressWarnings("unused")
    private TokenExchange tokenService;

    @Autowired
    private SykemeldingConsumer sykemeldingConsumer;

    private static final String IDENT = "12345678910";
    private static final String ORGNR = "123456789";
    private static final String ARBEIDSFORHOLD_ID = "1";
    private static final String SYKEMELDING_URL = "(.*)/sykemelding/sykemelding/api/v1/sykemeldinger";

    private SyntSykemeldingDTO dto;
    private PdlPerson pdlResponse;
    private ArbeidsforholdDTO arbeidsforholdResponse;
    private OrganisasjonDTO organisasjonResponse;
    private Map<String, SyntSykemeldingHistorikkDTO> syntResponse;
    private HelsepersonellListeDTO helsepersonellResponse;
    private SykemeldingDTO sykemeldingRequest;

    @Before
    public void setUp() {
        when(tokenService.exchange(ArgumentMatchers.any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));

        dto = SyntSykemeldingDTO.builder()
                .arbeidsforholdId(ARBEIDSFORHOLD_ID)
                .ident(IDENT)
                .orgnummer(ORGNR)
                .startDato(LocalDate.now())
                .build();

        pdlResponse = getTestPdlPerson(IDENT);
        arbeidsforholdResponse = getTestArbeidsforholdDTO(ARBEIDSFORHOLD_ID, ORGNR);
        organisasjonResponse = getTestOrganisasjonDTO(ORGNR);

        var arbeidsforhold = new Arbeidsforhold(
                arbeidsforholdResponse,
                organisasjonResponse
        );

        syntResponse = getTestHistorikk(IDENT);
        helsepersonellResponse = getTestLegeListeDTO();

        sykemeldingRequest = new Sykemelding(
                new Person(pdlResponse),
                syntResponse.get(IDENT),
                dto,
                new Helsepersonell(helsepersonellResponse.getHelsepersonell().get(0)),
                arbeidsforhold).toDTO();
    }

    @BeforeEach
    public void reset(){
        WireMock.reset();
    }

    @Test
    public void shouldPostSykemeldingUtenFeil() {
        stubSykemelding();
        assertDoesNotThrow(() -> sykemeldingConsumer.opprettSykemelding(sykemeldingRequest));
    }

    @Test
    public void shouldGetFeil() {
        stubSykemeldingError();
        assertThrows(ResponseStatusException.class, () -> sykemeldingConsumer.opprettSykemelding(sykemeldingRequest));
    }

    private void stubSykemelding() {
        stubFor(post(urlPathMatching(SYKEMELDING_URL)).willReturn(ok()));
    }

    private void stubSykemeldingError() {
        stubFor(post(urlPathMatching(SYKEMELDING_URL)).willReturn(aResponse().withStatus(500)));
    }

}

