//package no.nav.testnav.apps.syntsykemeldingapi.provider;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.github.tomakehurst.wiremock.client.WireMock;
//import no.nav.testnav.apps.syntsykemeldingapi.consumer.dto.SyntSykemeldingHistorikkDTO;
//import no.nav.testnav.apps.syntsykemeldingapi.domain.Arbeidsforhold;
//import no.nav.testnav.apps.syntsykemeldingapi.domain.Helsepersonell;
//import no.nav.testnav.apps.syntsykemeldingapi.domain.Person;
//import no.nav.testnav.apps.syntsykemeldingapi.domain.Sykemelding;
//import no.nav.testnav.apps.syntsykemeldingapi.domain.pdl.PdlPerson;
//import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;
//import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;
//import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
//import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;
//import no.nav.testnav.libs.dto.synt.sykemelding.v1.SyntSykemeldingDTO;
//import no.nav.testnav.libs.securitycore.domain.AccessToken;
//import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.ArgumentMatchers;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.time.LocalDate;
//import java.util.Map;
//
//import static com.github.tomakehurst.wiremock.client.WireMock.*;
//import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.*;
//
//@ActiveProfiles("test")
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(locations = "classpath:application-test.yml")
//@AutoConfigureWireMock(port = 0)
//public class SyntSykemeldingControllerTest {
//
//    @MockBean
//    private JwtDecoder jwtDecoder;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private SyntSykemeldingController syntSykemeldingController;
//
//    private static final String ident = "01019049945";
//    private static final String orgnr = "123456789";
//    private static final String arbeidsforholdId = "1";
//
//    private static final String pdlProxyUrl = "(.*)/pdl/pdl-api/graphql";
//    private static final String arbeidsforholdUrl = "(.*)/arbeidsforhold/api/v1/arbeidsforhold/" + ident + "/" + orgnr + "/" + arbeidsforholdId;
//    private static final String organisasjonUrl = "(.*)/organisasjon/api/v1/organisasjoner/" + orgnr;
//    private static final String syntUrl = "(.*)/synt/api/v1/generate_sykmeldings_history_json";
//    private static final String helsepersonellUrl = "(.*)/testnav-helsepersonell/api/v1/helsepersonell";
//    private static final String sykemeldingUrl = "(.*)/sykemelding/sykemelding/api/v1/sykemeldinger";
//
//    private SyntSykemeldingDTO dto;
//    private PdlPerson pdlResponse;
//    private ArbeidsforholdDTO arbeidsforholdResponse;
//    private OrganisasjonDTO organisasjonResponse;
//    private final Map<String, String> syntRequest = Map.of(ident, LocalDate.now().toString());
//    private Map<String, SyntSykemeldingHistorikkDTO> syntResponse;
//    private HelsepersonellListeDTO helsepersonellResponse;
//    private SykemeldingDTO sykemeldingRequest;
//    private final String tokenResponse = "{\"access_token\": \"dummy\"}";
//
//    @Before
//    public void before() {
//        WireMock.reset();
//        dto = SyntSykemeldingDTO.builder()
//                .arbeidsforholdId(arbeidsforholdId)
//                .ident(ident)
//                .orgnummer(orgnr)
//                .startDato(LocalDate.now())
//                .build();
//
//        pdlResponse = getTestPdlPerson(ident);
//        arbeidsforholdResponse = getTestArbeidsforholdDTO(arbeidsforholdId, orgnr);
//        organisasjonResponse = getTestOrganisasjonDTO(orgnr);
//
//        var arbeidsforhold = new Arbeidsforhold(
//                arbeidsforholdResponse,
//                organisasjonResponse
//        );
//
//        syntResponse = getTestHistorikk(ident);
//        helsepersonellResponse = getTestLegeListeDTO();
//
//        sykemeldingRequest = new Sykemelding(
//                new Person(pdlResponse),
//                syntResponse.get(ident),
//                dto,
//                new Helsepersonell(helsepersonellResponse.getHelsepersonell().get(0)),
//                arbeidsforhold).toDTO();
//    }
//
//    @Test
//    public void shouldSendeInnSykemeldinger() throws JsonProcessingException {
//        stubToken();
//        stubPdlProxy();
//        stubArbeidsforhold();
//        stubOrgansisasjon();
//        stubSynt();
//        stubHelsepersonell();
//        stubSykemelding();
//
//        syntSykemeldingController.opprett(dto);
//    }
//
//    private void stubToken() {
//        stubFor(post("/aad/oauth2/v2.0/token").willReturn(okJson(tokenResponse)));
//    }
//
//    private void stubPdlProxy() throws JsonProcessingException {
//        stubFor(post(urlPathMatching(pdlProxyUrl))
//                .willReturn(ok()
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(objectMapper.writeValueAsString(pdlResponse))));
//    }
//
//    private void stubArbeidsforhold() throws JsonProcessingException {
//        stubFor(get(urlPathMatching(arbeidsforholdUrl))
//                .willReturn(ok()
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(objectMapper.writeValueAsString(arbeidsforholdResponse))));
//    }
//
//    private void stubOrgansisasjon() throws JsonProcessingException {
//        stubFor(get(urlPathMatching(organisasjonUrl))
//                .willReturn(ok()
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(objectMapper.writeValueAsString(organisasjonResponse))));
//    }
//
//    private void stubSynt() throws JsonProcessingException {
//        stubFor(post(urlPathMatching(syntUrl))
//                .withRequestBody(equalToJson(objectMapper.writeValueAsString(syntRequest)))
//                .willReturn(ok()
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(objectMapper.writeValueAsString(syntResponse))));
//    }
//
//    private void stubHelsepersonell() throws JsonProcessingException {
//        stubFor(get(urlPathMatching(helsepersonellUrl))
//                .willReturn(ok()
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(objectMapper.writeValueAsString(helsepersonellResponse))));
//    }
//
//    private void stubSykemelding() throws JsonProcessingException {
//        stubFor(post(urlPathMatching(sykemeldingUrl))
//                .withRequestBody(equalToJson(objectMapper.writeValueAsString(sykemeldingRequest)))
//                .willReturn(ok()));
//    }
//
//}
