package no.nav.dolly.bestilling.kontoregisterservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.kontoregisterservice.command.SendHentKontoregisterCommand;
import no.nav.dolly.bestilling.kontoregisterservice.command.SendOppdaterKontoregisterCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.KontoregisterLandkode;
import no.nav.dolly.config.credentials.KontoregisterConsumerProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.HentKontoRequestDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.HentKontoResponseDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.OppdaterKontoRequestDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class KontoregisterConsumer {

    private static final int IBAN_COUNTRY_LENGTH = 2;
    private static final int DEFAULT_ACCOUNT_LENGTH = 15;
    private static final int NORSK_ACCOUNT_LENGTH = 11;

    private static final Random random = new SecureRandom();

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serviceProperties;
    private final MapperFacade mapperFacade;

    public KontoregisterConsumer(TokenExchange tokenService,
                                 KontoregisterConsumerProperties serverProperties,
                                 ObjectMapper objectMapper,
                                 ExchangeFilterFunction metricsWebClientFilterFunction,
                                 MapperFacade mapperFacade) {

        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.mapperFacade = mapperFacade;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public static String tilfeldigUtlandskBankkonto(String landkode) {
        if (nonNull(landkode) && landkode.length() == 3) {
            landkode = KontoregisterLandkode.getIso2FromIso(landkode);
        }
        if (nonNull(landkode) && landkode.length() > 2) {
            landkode = landkode.substring(0, 2);
        }

        var kontonummerLengde = DEFAULT_ACCOUNT_LENGTH;

        try {
            var kontoregisterLandkode = KontoregisterLandkode.valueOf(landkode);
            if (nonNull(kontoregisterLandkode.getIbanLengde()) && kontoregisterLandkode.getIbanLengde() > 2) {
                kontonummerLengde = kontoregisterLandkode.getIbanLengde() - IBAN_COUNTRY_LENGTH;
            }
        } catch (Exception e) {
            log.warn("bruker ukjent 'landkode' {} for generere kontonummer", landkode);
        }

        var kontonummer = random.ints(kontonummerLengde, 0, 10)
                .boxed()
                .map(Integer::toUnsignedString)
                .collect(Collectors.joining());

        return landkode + kontonummer;
    }

    public static String tilfeldigNorskBankkonto() {
        var kontonummerLengde = NORSK_ACCOUNT_LENGTH - 1;

        var kontonummer = random.ints(kontonummerLengde, 0, 9)
                .boxed()
                .map(Integer::toUnsignedString)
                .collect(Collectors.joining());

        var checkDigit = NorskBankkontoGenerator.getCheckDigit(kontonummer);

        if (checkDigit == '-') {
            kontonummer = random.ints(kontonummerLengde, 0, 9)
                    .boxed()
                    .map(Integer::toUnsignedString)
                    .collect(Collectors.joining());
        }

        return kontonummer + NorskBankkontoGenerator.getCheckDigit(kontonummer);
    }

    @Timed(name = "providers", tags = {"operation", "kontoregister_createUtenlandskBankkonto"})
    public Mono<String> sendUtenlandskBankkontoRequest(String ident, BankkontonrUtlandDTO bankkonto) {
        var requestDto = mapperFacade.map(bankkonto, OppdaterKontoRequestDTO.class);
        requestDto.setKontohaver(ident);

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new SendOppdaterKontoregisterCommand(webClient, requestDto, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "kontoregister_createNorskBankkonto"})
    public Mono<String> sendNorskBankkontoRequest(String ident, BankkontonrNorskDTO body) {
        var requestDto = mapperFacade.map(body, OppdaterKontoRequestDTO.class);
        requestDto.setKontohaver(ident);

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new SendOppdaterKontoregisterCommand(webClient, requestDto, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "kontoregister_hentKonto"})
    public Mono<HentKontoResponseDTO> sendHentKontoRequest(String ident) {
        var requestDto = new HentKontoRequestDTO(ident, false);

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new SendHentKontoregisterCommand(webClient, requestDto, token.getTokenValue()).call());
    }

    public static class NorskBankkontoGenerator {
        private static int getWeightNumber(int i) {
            return 7 - (i + 2) % 6;
        }

        public static char getCheckDigit(String kontonummer) {
            int lastIndex = kontonummer.length() - 1;
            int sum = 0;

            for (int i = lastIndex; i >= 0; i--) {
                sum += Character.getNumericValue(kontonummer.charAt(i)) * getWeightNumber(i);
            }

            int remainder = sum % 11;

            if (remainder == 0) {
                return '0';
            }
            if (remainder == 1) {
                return '-';
            }
            return Character.forDigit(11 - remainder, 10);
        }
    }
}
