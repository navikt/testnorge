package no.nav.dolly.bestilling.kontoregisterservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.tpsmessagingservice.KontoregisterLandkode;
import no.nav.dolly.config.credentials.KontoregisterConsumerProperties;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.HentKontoRequestDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.OppdaterKontoRequestDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.OppdaterKontoResponseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
class KontoregisterConsumerTest {
    private static final String IDENT = "12345678901";
    private static final String KONTONUMMER = "1234567890";

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @Autowired
    private KontoregisterConsumer kontoregisterConsumer;

    @Autowired
    private MapperFacade mapperFacade;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {

        when(tokenService.exchange(ArgumentMatchers.any(KontoregisterConsumerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @AfterEach
    public void cleanUp() {
        WireMock.getAllServeEvents()
                .stream().forEach(e -> WireMock.removeServeEvent(e.getId()));
    }

    @Test
    void testBankkontoMapping() {
        var bankkonto1 = new BankkontonrUtlandDTO();

        bankkonto1.setTilfeldigKontonummer(true);
        bankkonto1.setLandkode("SE");

        var mapped1 = mapperFacade.map(bankkonto1, OppdaterKontoRequestDTO.class);
        assertThat("genererer kontonummer", StringUtils.isNoneBlank(mapped1.getKontonummer()));
        assertThat("generert kontonummer starter med SE", mapped1.getKontonummer().startsWith("SE"));

        var bankkonto2 = new BankkontonrUtlandDTO();

        bankkonto2.setKontonummer("123");
        bankkonto2.setLandkode("SWE");

        var mapped2 = mapperFacade.map(bankkonto2, OppdaterKontoRequestDTO.class);
        assertThat("kontonummer er 123", "123".equals(mapped2.getKontonummer()));
        assertThat("landkode er SE", "SE".equals(mapped2.getUtenlandskKonto().getBankLandkode()));
    }

    @Test
    void generateDifferentBankkonto() {
        var kontoer = IntStream.range(1, 100).boxed()
                .map((i) -> KontoregisterConsumer.tilfeldigUtlandskBankkonto(KontoregisterLandkode.SE.name()))
                .sorted()
                .collect(Collectors.toList());

        var unikKontoer = kontoer.stream().distinct().count();

        assertThat("forskjellige kontoer", unikKontoer == kontoer.size());
    }

    @Test
    void generateBankkontoWithUknownLandkode() {
        var konto = KontoregisterConsumer.tilfeldigUtlandskBankkonto("AABB");
        assertThat("konto har data", !konto.isEmpty());
        assertThat("konto begynner med AA", konto.substring(0, 2).equals("AA"));
    }

    @Test
    void generateBankkontoWithIsoLandkode() {
        var konto = KontoregisterConsumer.tilfeldigUtlandskBankkonto("SWE");
        assertThat("konto begynner med SE", konto.substring(0, 2).equals("SE"));
    }

    private String hentKontoResponse() {
        return "{\n" +
                "    \"aktivKonto\": {\n" +
                "        \"kontohaver\": \"" + IDENT + "\",\n" +
                "        \"kontonummer\": \"" + KONTONUMMER + "\",\n" +
                "        \"gyldigFom\": \"2022-08-16T14:15:05.573486\",\n" +
                "        \"opprettetAv\": \"Dolly\",\n" +
                "        \"utenlandskKontoInfo\": {\n" +
                "            \"banknavn\": \"string\",\n" +
                "            \"bankkode\": \"XXXX\",\n" +
                "            \"bankLandkode\": \"SE\",\n" +
                "            \"valutakode\": \"SEK\",\n" +
                "            \"swiftBicKode\": \"SHEDSE22\",\n" +
                "            \"bankadresse1\": \"string\",\n" +
                "            \"bankadresse2\": \"string\",\n" +
                "            \"bankadresse3\": \"string\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    @Test
    void generateTilfeldigUtenlandskbankkonto() {
        stubFor(
                post(urlPathMatching("(.*)/api/kontoregister/v1/oppdater-konto"))
                        .willReturn(ok()
                                .withBody("")
                                .withHeader("Content-Type", "application/json")));

        var dto = new BankkontonrUtlandDTO();
        dto.setTilfeldigKontonummer(true);

        List.of("1111111111", "2222222222", "333333333", "4444444444")
                .parallelStream()
                .forEach(
                        p -> kontoregisterConsumer.sendUtenlandskBankkontoRequest(p, dto)
                );

        var sendtBankkontoer = WireMock.getAllServeEvents()
                .stream()
                .map(e -> e.getRequest().getBodyAsString())
                .map(s -> {
                    try {
                        return objectMapper.readValue(s, OppdaterKontoResponseDTO.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(k -> k.getKontonummer())
                .collect(Collectors.toList());

        var forskjelligeBankkontoer = sendtBankkontoer.stream().distinct().collect(Collectors.toList());

        assertThat("tilfeldig kontonummer is True", dto.getTilfeldigKontonummer());
        assertThat("sendt forskjellige kontonummer", forskjelligeBankkontoer.size() == sendtBankkontoer.size());
    }

    @Test
    void oppdaterNorskbankkonto() {
        stubFor(
                post(urlPathMatching("(.*)/api/kontoregister/v1/oppdater-konto"))
                        .willReturn(ok()
                                .withBody("")
                                .withHeader("Content-Type", "application/json")));

        var dto = new BankkontonrNorskDTO();
        dto.setKontonummer(KONTONUMMER);

        List.of(IDENT)
                .parallelStream()
                .forEach(
                        p -> kontoregisterConsumer.sendNorskBankkontoRequest(p, dto)
                );

        var sendtBankkontoer = WireMock.getAllServeEvents()
                .stream()
                .map(e -> e.getRequest().getBodyAsString())
                .map(s -> {
                    try {
                        return objectMapper.readValue(s, OppdaterKontoResponseDTO.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        sendtBankkontoer.stream()
                .forEach(b-> {
                    assertThat("sendt kontonummer er riktig", KONTONUMMER.equals(b.getKontonummer()));
                    assertThat("sendt ident er riktig", IDENT.equals(b.getKontohaver()));
                    assertThat("opprettet av Dolly", "Dolly".equals(b.getOpprettetAv()));
                    assertThat("utenlandskKonto er null", b.getUtenlandskKonto() == null);
                });


    }

    @Test
    void oppdaterUtenlandskbankkonto() {
        stubFor(
                post(urlPathMatching("(.*)/api/system/v1/oppdater-konto"))
                        .willReturn(ok()
                                .withBody("")
                                .withHeader("Content-Type", "application/json")));

        var dto = new BankkontonrUtlandDTO();
        dto.setKontonummer(KONTONUMMER);
        dto.setSwift("SHEDSE22");
        dto.setValuta("SEK");
        dto.setLandkode("SE");
        dto.setBanknavn("banknavn");
        dto.setBankAdresse1("address1");
        dto.setBankAdresse2("address2");
        dto.setBankAdresse3("address3");

        List.of(IDENT)
                .parallelStream()
                .forEach(
                        p -> kontoregisterConsumer.sendUtenlandskBankkontoRequest(p, dto)
                );

        var sendtBankkontoer = WireMock.getAllServeEvents()
                .stream()
                .map(e -> e.getRequest().getBodyAsString())
                .map(s -> {
                    try {
                        return objectMapper.readValue(s, OppdaterKontoResponseDTO.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        sendtBankkontoer.stream()
                .forEach(b-> {
                    assertThat("sendt kontonummer er riktig", KONTONUMMER.equals(b.getKontonummer()));
                    assertThat("sendt ident er riktig", IDENT.equals(b.getKontohaver()));
                    assertThat("opprettet av Dolly", "Dolly".equals(b.getOpprettetAv()));
                    assertThat("utenlandskKonto er ikke null", b.getUtenlandskKonto() != null);

                    var utenlandsk = b.getUtenlandskKonto();
                    assertThat("swift", "SHEDSE22".equals(utenlandsk.getSwiftBicKode()));
                    assertThat("valuta", "SEK".equals(utenlandsk.getValutakode()));
                    assertThat("landkode", "SE".equals(utenlandsk.getBankLandkode()));
                    assertThat("banknavn", "banknavn".equals(utenlandsk.getBanknavn()));
                    assertThat("address1", "address1".equals(utenlandsk.getBankadresse1()));
                    assertThat("address2", "address2".equals(utenlandsk.getBankadresse2()));
                    assertThat("address3", "address3".equals(utenlandsk.getBankadresse3()));
                });
    }

    @Test
    void testHentBankkonto() {
        stubFor(
                post(urlPathMatching("(.*)/api/system/v1/hent-konto"))
                        .willReturn(ok()
                                .withBody(hentKontoResponse())
                                .withHeader("Content-Type", "application/json")));

        var hentResponse = kontoregisterConsumer.sendHentKontoRequest(IDENT).block();

        var hentBankkontoer = WireMock.getAllServeEvents()
                .stream()
                .map(e -> e.getRequest().getBodyAsString())
                .map(s -> {
                    try {
                        return objectMapper.readValue(s, HentKontoRequestDTO.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        hentBankkontoer.stream()
                .forEach(b-> {
                    assertThat("sendt ident er riktig", IDENT.equals(b.getKontohaver()));
                });

        assertThat("kontonummer", KONTONUMMER.equals(hentResponse.getAktivKonto().getKontonummer()));
        assertThat("ident", IDENT.equals(hentResponse.getAktivKonto().getKontohaver()));
        assertThat("bankkode", "XXXX".equals(hentResponse.getAktivKonto().getUtenlandskKontoInfo().getBankkode()));
        assertThat("swift", "SHEDSE22".equals(hentResponse.getAktivKonto().getUtenlandskKontoInfo().getSwiftBicKode()));
    }
}
