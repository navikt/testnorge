package no.nav.pdl.forvalter.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.config.Consumers;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdresseServiceConsumerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Mock
    private TokenExchange tokenExchange;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private KodeverkConsumer kodeverkConsumer;

    @Mock
    private Consumers consumers;

    @Mock
    private ExchangeFunction exchangeFunction;

    private AdresseServiceConsumer adresseServiceConsumer;

    @BeforeEach
    void setUp() {
        var serverProperties = ServerProperties.of("dev-gcp", "dolly", "adresse-service", "http://localhost:8080");

        when(consumers.getAdresseService()).thenReturn(serverProperties);

        var webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();

        when(tokenExchange.exchange(any(ServerProperties.class)))
                .thenReturn(Mono.just(new AccessToken("test-token")));

        adresseServiceConsumer = new AdresseServiceConsumer(
                tokenExchange,
                consumers,
                webClient,
                mapperFacade,
                kodeverkConsumer
        );
    }

    @Test
    void shouldReturnVegadresseFromService() throws JsonProcessingException {

        var inputVegadresse = VegadresseDTO.builder()
                .kommunenummer("0301")
                .build();

        var mappedVegadresse = VegadresseDTO.builder()
                .kommunenummer("0301")
                .build();

        when(mapperFacade.map(inputVegadresse, VegadresseDTO.class)).thenReturn(mappedVegadresse);
        when(kodeverkConsumer.getKommunerMedHistoriske())
                .thenReturn(Mono.just(Map.of("0301", "Oslo")));

        var responseAdresse = no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO.builder()
                .matrikkelId("12345")
                .adressenavn("TESTGATA")
                .postnummer("0101")
                .husnummer(1)
                .kommunenummer("0301")
                .poststed("Oslo")
                .build();

        mockExchangeResponse(new no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO[]{responseAdresse});

        StepVerifier.create(adresseServiceConsumer.getVegadresse(inputVegadresse, null))
                .assertNext(result -> {
                    assertThat(result.getAdressenavn()).isEqualTo("TESTGATA");
                    assertThat(result.getKommunenummer()).isEqualTo("0301");
                    assertThat(result.getPostnummer()).isEqualTo("0101");
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnDefaultVegadresseWhenServiceReturnsEmpty() throws JsonProcessingException {

        var inputVegadresse = VegadresseDTO.builder()
                .kommunenummer("0301")
                .build();

        var mappedVegadresse = VegadresseDTO.builder()
                .kommunenummer("0301")
                .build();

        when(mapperFacade.map(inputVegadresse, VegadresseDTO.class)).thenReturn(mappedVegadresse);
        when(kodeverkConsumer.getKommunerMedHistoriske())
                .thenReturn(Mono.just(Map.of("0301", "Oslo")));

        mockExchangeResponse(new no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO[]{});

        StepVerifier.create(adresseServiceConsumer.getVegadresse(inputVegadresse, null))
                .assertNext(result -> {
                    assertThat(result.getAdressenavn()).isEqualTo("FYRSTIKKALLÉEN");
                    assertThat(result.getKommunenummer()).isEqualTo("0301");
                    assertThat(result.getPostnummer()).isEqualTo("0661");
                })
                .verifyComplete();
    }

    @Test
    void shouldSetKommunenummerToNullWhenUoppgittForVegadresse() throws JsonProcessingException {

        var inputVegadresse = VegadresseDTO.builder()
                .kommunenummer("9999")
                .build();

        var mappedVegadresse = VegadresseDTO.builder()
                .kommunenummer("9999")
                .build();

        when(mapperFacade.map(inputVegadresse, VegadresseDTO.class)).thenReturn(mappedVegadresse);

        var responseAdresse = no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO.builder()
                .matrikkelId("12345")
                .adressenavn("TESTGATA")
                .postnummer("0101")
                .kommunenummer("0301")
                .build();

        mockExchangeResponse(new no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO[]{responseAdresse});

        StepVerifier.create(adresseServiceConsumer.getVegadresse(inputVegadresse, null))
                .assertNext(result -> {
                    assertThat(mappedVegadresse.getKommunenummer()).isNull();
                    assertThat(result.getAdressenavn()).isEqualTo("TESTGATA");
                })
                .verifyComplete();
    }

    @Test
    void shouldNotOverrideKommunenummerWhenDefaultVegadresseReturned() throws JsonProcessingException {

        var inputVegadresse = VegadresseDTO.builder()
                .kommunenummer("1103")
                .build();

        var mappedVegadresse = VegadresseDTO.builder()
                .kommunenummer("1103")
                .build();

        when(mapperFacade.map(inputVegadresse, VegadresseDTO.class)).thenReturn(mappedVegadresse);
        when(kodeverkConsumer.getKommunerMedHistoriske())
                .thenReturn(Mono.just(Map.of("1103", "Stavanger")));

        mockExchangeResponse(new no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO[]{});

        StepVerifier.create(adresseServiceConsumer.getVegadresse(inputVegadresse, null))
                .assertNext(result -> {
                    assertThat(result.getAdressenavn()).isEqualTo("FYRSTIKKALLÉEN");
                    assertThat(result.getKommunenummer()).isEqualTo("0301");
                })
                .verifyComplete();
    }

    @Test
    void shouldPreserveOriginalKommunenummerForNonDefaultVegadresse() throws JsonProcessingException {

        var inputVegadresse = VegadresseDTO.builder()
                .kommunenummer("4601")
                .build();

        var mappedVegadresse = VegadresseDTO.builder()
                .kommunenummer("4601")
                .build();

        when(mapperFacade.map(inputVegadresse, VegadresseDTO.class)).thenReturn(mappedVegadresse);
        when(kodeverkConsumer.getKommunerMedHistoriske())
                .thenReturn(Mono.just(Map.of("4601", "Bergen")));

        var responseAdresse = no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO.builder()
                .matrikkelId("99999")
                .adressenavn("STRANDGATEN")
                .postnummer("5003")
                .kommunenummer("9999")
                .build();

        mockExchangeResponse(new no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO[]{responseAdresse});

        StepVerifier.create(adresseServiceConsumer.getVegadresse(inputVegadresse, null))
                .assertNext(result -> {
                    assertThat(result.getAdressenavn()).isEqualTo("STRANDGATEN");
                    assertThat(result.getKommunenummer()).isEqualTo("4601");
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnMatrikkeladresseFromService() throws JsonProcessingException {

        var inputAdresse = MatrikkeladresseDTO.builder()
                .kommunenummer("0301")
                .build();

        when(mapperFacade.map(inputAdresse, MatrikkeladresseDTO.class)).thenReturn(inputAdresse);
        when(kodeverkConsumer.getKommunerMedHistoriske())
                .thenReturn(Mono.just(Map.of("0301", "Oslo")));

        var responseAdresse = no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO.builder()
                .matrikkelId("67890")
                .kommunenummer("0301")
                .gaardsnummer("100")
                .bruksnummer("50")
                .postnummer("0580")
                .poststed("Oslo")
                .build();

        mockExchangeResponse(new no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO[]{responseAdresse});

        StepVerifier.create(adresseServiceConsumer.getMatrikkeladresse(inputAdresse, null))
                .assertNext(result -> {
                    assertThat(result.getMatrikkelId()).isEqualTo("67890");
                    assertThat(result.getKommunenummer()).isEqualTo("0301");
                    assertThat(result.getGaardsnummer()).isEqualTo("100");
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnDefaultMatrikkeladresseWhenServiceReturnsEmpty() throws JsonProcessingException {

        var inputAdresse = MatrikkeladresseDTO.builder()
                .kommunenummer("0301")
                .build();

        when(mapperFacade.map(inputAdresse, MatrikkeladresseDTO.class)).thenReturn(inputAdresse);
        when(kodeverkConsumer.getKommunerMedHistoriske())
                .thenReturn(Mono.just(Map.of("0301", "Oslo")));

        mockExchangeResponse(new no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO[]{});

        StepVerifier.create(adresseServiceConsumer.getMatrikkeladresse(inputAdresse, null))
                .assertNext(result -> {
                    assertThat(result.getTilleggsnavn()).isEqualTo("VALEN");
                    assertThat(result.getKommunenummer()).isEqualTo("4626");
                    assertThat(result.getMatrikkelId()).isEqualTo("24867173");
                })
                .verifyComplete();
    }

    @Test
    void shouldSetKommunenummerToNullWhenUoppgittForMatrikkeladresse() throws JsonProcessingException {

        var inputAdresse = MatrikkeladresseDTO.builder()
                .kommunenummer("9999")
                .build();

        when(mapperFacade.map(inputAdresse, MatrikkeladresseDTO.class)).thenReturn(inputAdresse);

        var responseAdresse = no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO.builder()
                .matrikkelId("12345")
                .kommunenummer("4626")
                .tilleggsnavn("TESTSTUA")
                .build();

        mockExchangeResponse(new no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO[]{responseAdresse});

        StepVerifier.create(adresseServiceConsumer.getMatrikkeladresse(inputAdresse, null))
                .assertNext(result -> {
                    assertThat(inputAdresse.getKommunenummer()).isNull();
                    assertThat(result.getTilleggsnavn()).isEqualTo("TESTSTUA");
                })
                .verifyComplete();
    }

    @Test
    void shouldNotOverrideKommunenummerWhenDefaultMatrikkeladresseReturned() throws JsonProcessingException {

        var inputAdresse = MatrikkeladresseDTO.builder()
                .kommunenummer("4626")
                .build();

        when(mapperFacade.map(inputAdresse, MatrikkeladresseDTO.class)).thenReturn(inputAdresse);
        when(kodeverkConsumer.getKommunerMedHistoriske())
                .thenReturn(Mono.just(Map.of("4626", "Øygarden")));

        mockExchangeResponse(new no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO[]{});

        StepVerifier.create(adresseServiceConsumer.getMatrikkeladresse(inputAdresse, null))
                .assertNext(result -> {
                    assertThat(result.getTilleggsnavn()).isEqualTo("VALEN");
                    assertThat(result.getKommunenummer()).isEqualTo("4626");
                })
                .verifyComplete();
    }

    @Test
    void shouldPreserveOriginalKommunenummerForNonDefaultMatrikkeladresse() throws JsonProcessingException {

        var inputAdresse = MatrikkeladresseDTO.builder()
                .kommunenummer("5001")
                .build();

        when(mapperFacade.map(inputAdresse, MatrikkeladresseDTO.class)).thenReturn(inputAdresse);
        when(kodeverkConsumer.getKommunerMedHistoriske())
                .thenReturn(Mono.just(Map.of("5001", "Trondheim")));

        var responseAdresse = no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO.builder()
                .matrikkelId("77777")
                .kommunenummer("9999")
                .tilleggsnavn("NORDRE")
                .build();

        mockExchangeResponse(new no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO[]{responseAdresse});

        StepVerifier.create(adresseServiceConsumer.getMatrikkeladresse(inputAdresse, null))
                .assertNext(result -> {
                    assertThat(result.getKommunenummer()).isEqualTo("5001");
                    assertThat(result.getTilleggsnavn()).isEqualTo("NORDRE");
                })
                .verifyComplete();
    }

    @Test
    void shouldResolveHistoriskKommuneForVegadresse() throws JsonProcessingException {

        var inputVegadresse = VegadresseDTO.builder()
                .kommunenummer("0228")
                .build();

        var mappedVegadresse = VegadresseDTO.builder()
                .kommunenummer("0228")
                .build();

        when(mapperFacade.map(inputVegadresse, VegadresseDTO.class)).thenReturn(mappedVegadresse);
        when(kodeverkConsumer.getKommunerMedHistoriske())
                .thenReturn(Mono.just(Map.of(
                        "0228", "Røyken(historisk)",
                        "3025", "Asker"
                )));

        var responseAdresse = no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO.builder()
                .matrikkelId("55555")
                .adressenavn("SLOTTSVEIEN")
                .kommunenummer("3025")
                .build();

        mockExchangeResponse(new no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO[]{responseAdresse});

        StepVerifier.create(adresseServiceConsumer.getVegadresse(inputVegadresse, null))
                .assertNext(result -> {
                    assertThat(result.getAdressenavn()).isEqualTo("SLOTTSVEIEN");
                    assertThat(result.getKommunenummer()).isEqualTo("0228");
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleNullKommunenummerForVegadresse() throws JsonProcessingException {

        var inputVegadresse = VegadresseDTO.builder()
                .build();

        var mappedVegadresse = VegadresseDTO.builder()
                .build();

        when(mapperFacade.map(inputVegadresse, VegadresseDTO.class)).thenReturn(mappedVegadresse);

        var responseAdresse = no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO.builder()
                .adressenavn("PARKVEIEN")
                .kommunenummer("0301")
                .build();

        mockExchangeResponse(new no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO[]{responseAdresse});

        StepVerifier.create(adresseServiceConsumer.getVegadresse(inputVegadresse, null))
                .assertNext(result ->
                        assertThat(result.getAdressenavn()).isEqualTo("PARKVEIEN"))
                .verifyComplete();
    }

    @Test
    void shouldHandleNullKommunenummerForMatrikkeladresse() throws JsonProcessingException {

        var inputAdresse = MatrikkeladresseDTO.builder()
                .build();

        when(mapperFacade.map(inputAdresse, MatrikkeladresseDTO.class)).thenReturn(inputAdresse);

        var responseAdresse = no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO.builder()
                .matrikkelId("88888")
                .kommunenummer("4626")
                .build();

        mockExchangeResponse(new no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO[]{responseAdresse});

        StepVerifier.create(adresseServiceConsumer.getMatrikkeladresse(inputAdresse, null))
                .assertNext(result ->
                        assertThat(result.getMatrikkelId()).isEqualTo("88888"))
                .verifyComplete();
    }

    private <T> void mockExchangeResponse(T body) throws JsonProcessingException {

        var json = OBJECT_MAPPER.writeValueAsString(body);

        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(ClientResponse.create(HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(json)
                        .build()));
    }
}
