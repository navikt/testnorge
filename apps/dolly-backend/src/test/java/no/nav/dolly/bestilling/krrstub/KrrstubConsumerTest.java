package no.nav.dolly.bestilling.krrstub;

import no.nav.dolly.config.credentials.KrrstubProxyProperties;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
public class KrrstubConsumerTest {

    private static final String EPOST = "morro.pa@landet.no";
    private static final String MOBIL = "11111111";
    private static final String IDENT = "12345678901";
    private static final boolean RESERVERT = true;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @Autowired
    private KrrstubConsumer krrStubConsumer;

    @BeforeEach
    public void setup() {

        when(tokenService.exchange(ArgumentMatchers.any(KrrstubProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

//    @Test
//    public void createDigitalKontaktdata_Ok() {
//
//        stubPostKrrData();
//
//        var response = StepVerifier.create(krrStubConsumer.createDigitalKontaktdata(DigitalKontaktdata.builder()
//                .epost(EPOST)
//                .mobil(MOBIL)
//                .reservert(RESERVERT)
//                .build()))
//                        .verifyComplete();
//        assertThat("Response should be 200 successful", response.getStatusCode().is2xxSuccessful());
//    }

//    @Test
//    public void deleteDigitalKontaktdata_Ok() {
//
//        stubDeleteKrrData();

//        var response = krrStubConsumer.deleteKontaktdata(List.of(IDENT)).block();
//
//        MatcherAssert.assertThat(response.size(), is(equalTo(1)));
//    }
//
//    @Test
//    void deleteDigitalKontaktdataPerson_Ok() {
//        var deleteStub = stubDeleteKrrData();
//
//        var response = krrStubConsumer.deleteKontaktdataPerson(List.of(IDENT)).block();
//
//        var deleteEvents = WireMock.getAllServeEvents().stream()
//                .filter(e -> e.getStubMapping().getId().equals(deleteStub.getId()))
//                .toList();
//
//        MatcherAssert.assertThat("delete event list er størrelsen av 1", deleteEvents.size(), is(equalTo(1)));
//        MatcherAssert.assertThat("response list er størrelsen av 1", response.size(), is(equalTo(1)));
//    }
//
//    @Test
//    public void createDigitalKontaktdata_GenerateTokenFailed_ThrowsDollyFunctionalException() {
//
//        when(tokenService.exchange(any(KrrstubProxyProperties.class))).thenReturn(Mono.empty());
//
//        Assertions.assertThrows(SecurityException.class, () -> krrStubConsumer.createDigitalKontaktdata(DigitalKontaktdata.builder()
//                .epost(EPOST)
//                .mobil(MOBIL)
//                .reservert(RESERVERT)
//                .build()));
//
//        verify(tokenService).exchange(any(KrrstubProxyProperties.class));
//    }
//
//    @Test
//    void feilmeldingVedHttp409() {
//
//        stubPostKrrDataMed409();
//
//        try {
//            ResponseEntity<Object> response = krrStubConsumer.createDigitalKontaktdata(DigitalKontaktdata.builder()
//                    .epost(EPOST)
//                    .mobil(MOBIL)
//                    .reservert(RESERVERT)
//                    .build());
//        } catch (WebClientResponseException e) {
//            assertThat("response status kode må være 409", e.getStatusCode().value() == 409);
//        }
//    }
//
//    private void stubPostKrrData() {
//
//        stubFor(post(urlPathMatching("(.*)/api/v2/kontaktinformasjon"))
//                .willReturn(ok()
//                        .withHeader("Content-Type", "application/json")));
//    }
//
//    private void stubPostKrrDataMed409() {
//
//        stubFor(post(urlPathMatching("(.*)/api/v2/kontaktinformasjon"))
//                .willReturn(aResponse()
//                        .withStatus(409)
//                        .withHeader("Content-Type", "application/json")));
//    }
//
//    private StubMapping stubDeleteKrrData() {
//
//        stubFor(delete(urlPathMatching("(.*)/api/v2/kontaktinformasjon/" + IDENT))
//                .willReturn(ok()
//                        .withHeader("Content-Type", "application/json")));
//
//        var deletStub = stubFor(delete(urlPathMatching("(.*)/api/v2/person/kontaktinformasjon"))
//                .willReturn(ok()
//                        .withHeader("Content-Type", "application/json")));
//
//        stubFor(get(urlPathMatching("(.*)/api/v2/person/kontaktinformasjon"))
//                .withHeader(HEADER_NAV_PERSON_IDENT, WireMock.equalTo(IDENT))
//                .willReturn(ok()
//                        .withHeader("Content-Type", "application/json")
//                        .withBody("{\"id\":1}")));
//
//        return deletStub;
//    }
}
