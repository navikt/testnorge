package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.testnav.apps.syntsykemeldingapi.config.credentials.SykemeldingProperties;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.dto.SyntSykemeldingHistorikkDTO;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Arbeidsforhold;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Helsepersonell;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Person;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Sykemelding;
import no.nav.testnav.apps.syntsykemeldingapi.domain.pdl.PdlPerson;
import no.nav.testnav.apps.syntsykemeldingapi.exception.LagreSykemeldingException;
import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;
import no.nav.testnav.libs.dto.synt.sykemelding.v1.SyntSykemeldingDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestPdlPerson;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestHistorikk;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestArbeidsforholdDTO;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestLegeListeDTO;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestOrganisasjonDTO;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureWireMock(port = 0)
public class SykemeldingConsumerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SykemeldingConsumer sykemeldingConsumer;

    private static final String ident = "12345678910";
    private static final String orgnr = "123456789";
    private static final String arbeidsforholdId = "1";
    private static final String sykemeldingUrl = "(.*)/sykemelding/sykemelding/api/v1/sykemeldinger";

    private SyntSykemeldingDTO dto;
    private PdlPerson pdlResponse;
    private ArbeidsforholdDTO arbeidsforholdResponse;
    private OrganisasjonDTO organisasjonResponse;
    private Map<String, SyntSykemeldingHistorikkDTO> syntResponse;
    private HelsepersonellListeDTO helsepersonellResponse;
    private SykemeldingDTO sykemeldingRequest;

    @Before
    public void setUp() {
        when(tokenService.exchange(ArgumentMatchers.any(SykemeldingProperties.class))).thenReturn(Mono.just(new AccessToken("token")));

        dto = SyntSykemeldingDTO.builder()
                .arbeidsforholdId(arbeidsforholdId)
                .ident(ident)
                .orgnummer(orgnr)
                .startDato(LocalDate.now())
                .build();

        pdlResponse = getTestPdlPerson(ident);
        arbeidsforholdResponse = getTestArbeidsforholdDTO(arbeidsforholdId, orgnr);
        organisasjonResponse = getTestOrganisasjonDTO(orgnr);

        var arbeidsforhold = new Arbeidsforhold(
                arbeidsforholdResponse,
                organisasjonResponse
        );

        syntResponse = getTestHistorikk(ident);
        helsepersonellResponse = getTestLegeListeDTO();

        sykemeldingRequest = new Sykemelding(
                new Person(pdlResponse),
                syntResponse.get(ident),
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
        assertThrows(LagreSykemeldingException.class, () -> sykemeldingConsumer.opprettSykemelding(sykemeldingRequest));
    }

    private void stubSykemelding() {
        stubFor(post(urlPathMatching(sykemeldingUrl)).willReturn(ok()));
    }

    private void stubSykemeldingError() {
        stubFor(post(urlPathMatching(sykemeldingUrl)).willReturn(aResponse().withStatus(500)));
    }

}

