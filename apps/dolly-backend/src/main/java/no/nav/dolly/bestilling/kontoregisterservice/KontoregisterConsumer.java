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
import org.apache.commons.lang3.BooleanUtils;
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

    @Timed(name = "providers", tags = {"operation", "kontoregister_createUtenlandskBankkonto"})
    public Mono<Void> sendUtenlandskBankkontoRequest(String ident, BankkontonrUtlandDTO bankkonto) {
        var requestDto = mapperFacade.map(bankkonto, OppdaterKontoRequestDTO.class);
        requestDto.setKontohaver(ident);

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new SendOppdaterKontoregisterCommand(webClient, requestDto, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "kontoregister_createNorskBankkonto"})
    public Mono<Void> sendNorskBankkontoRequest(String ident, BankkontonrNorskDTO body) {
        var requestDto = new OppdaterKontoRequestDTO(ident, body.getKontonummer(), "Dolly", null);

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new SendOppdaterKontoregisterCommand(webClient, requestDto, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "kontoregister_hentKonto"})
    public Mono<HentKontoResponseDTO> sendHentKontoRequest(String ident) {
        var requestDto = new HentKontoRequestDTO(ident, false);

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new SendHentKontoregisterCommand(webClient, requestDto, token.getTokenValue()).call());
    }
}
