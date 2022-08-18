package no.nav.dolly.bestilling.kontoregisterservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.kontoregisterservice.command.SendOppdaterKontoregisterCommand;
import no.nav.dolly.bestilling.kontoregisterservice.dto.OppdaterKontoRequestDto;
import no.nav.dolly.bestilling.kontoregisterservice.dto.UtenlandskKontoDto;
import no.nav.dolly.config.credentials.KontoregisterConsumerProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class KontoregisterConsumer {

    private static final Random random = new SecureRandom();

    private static final int KONTONUMMER_LENGDE = 15;

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;

    public KontoregisterConsumer(TokenExchange tokenService,
                                 KontoregisterConsumerProperties serverProperties,
                                 ObjectMapper objectMapper,
                                 ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    private static String tilfeldigUtlandskBankkonto() {
        return Stream.concat(
            random.ints(2, 'A', 'Z')
                    .boxed()
                    .map(i -> Character.toString(i.intValue()))
            ,
            random.ints(KONTONUMMER_LENGDE, 0, 10)
                    .boxed()
                    .map(Integer::toUnsignedString)
        ).collect(Collectors.joining());
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createUtenlandskBankkonto"})
    public List<TpsMeldingResponseDTO> sendUtenlandskBankkontoRequest(String ident, BankkontonrUtlandDTO body) {

        if ( nonNull(body.getTilfeldigKontonummer()) && body.getTilfeldigKontonummer()) {
            body = body.withKontonummer(tilfeldigUtlandskBankkonto());
        }

        var utenlandskKontoDto = new UtenlandskKontoDto(
                body.getBanknavn(),
                "", // bankkode
                body.getLandkode(),
                body.getValuta(),
                body.getSwift(),
                body.getBankAdresse1(),
                body.getBankAdresse2(),
                body.getBankAdresse3()
        );
        var requestDto = new OppdaterKontoRequestDto(ident, body.getKontonummer(), "Dolly", utenlandskKontoDto);

        return new SendOppdaterKontoregisterCommand(webClient, requestDto, serviceProperties.getAccessToken(tokenService)).call();
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createNorskBankkonto"})
    public List<TpsMeldingResponseDTO> sendNorskBankkontoRequest(String ident, BankkontonrNorskDTO body) {
        var requestDto = new OppdaterKontoRequestDto(ident, body.getKontonummer(), "Dolly", null);

        return new SendOppdaterKontoregisterCommand(webClient, requestDto, serviceProperties.getAccessToken(tokenService)).call();
    }
}
