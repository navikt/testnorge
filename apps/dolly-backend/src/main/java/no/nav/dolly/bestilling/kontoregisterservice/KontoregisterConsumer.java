package no.nav.dolly.bestilling.kontoregisterservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.kontoregisterservice.command.SendHentKontoregisterCommand;
import no.nav.dolly.bestilling.kontoregisterservice.command.SendOppdaterKontoregisterCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.KontoregisterLandkode;
import no.nav.dolly.config.credentials.KontoregisterConsumerProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.HentKontoRequestDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.HentKontoResponseDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.OppdaterKontoRequestDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.OppdaterKontoResponseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Collectors;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class KontoregisterConsumer {

    private static final Random random = new SecureRandom();

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;
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
        if (landkode != null && landkode.length() == 3) {
            landkode = KontoregisterLandkode.getIso2FromIso(landkode);
        }
        if (landkode != null && landkode.length() > 2) {
            landkode = landkode.substring(0, 2);
        }

        var kontonummerLengde = 15;

        try {
            var kontoregisterLandkode = KontoregisterLandkode.valueOf(landkode);
            if (kontoregisterLandkode.getIbanLengde() != null && kontoregisterLandkode.getIbanLengde() > 2) {
                kontonummerLengde = kontoregisterLandkode.getIbanLengde() - 2; // -2 fordi først 2 er landkode
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
    public OppdaterKontoResponseDTO sendUtenlandskBankkontoRequest(String ident, BankkontonrUtlandDTO bankkonto) {
        var requestDto = mapperFacade.map(bankkonto, OppdaterKontoRequestDTO.class);

        var kontonummer = BooleanUtils.isTrue(bankkonto.getTilfeldigKontonummer()) ?
                tilfeldigUtlandskBankkonto(bankkonto.getLandkode()) : bankkonto.getKontonummer();
        requestDto.setKontonummer(kontonummer);

        requestDto.setKontohaver(ident);

        return new SendOppdaterKontoregisterCommand(webClient, requestDto, serviceProperties.getAccessToken(tokenService)).call();
    }

    @Timed(name = "providers", tags = {"operation", "kontoregister_createNorskBankkonto"})
    public OppdaterKontoResponseDTO sendNorskBankkontoRequest(String ident, BankkontonrNorskDTO body) {
        var requestDto = new OppdaterKontoRequestDTO(ident, body.getKontonummer(), "Dolly", null);

        return new SendOppdaterKontoregisterCommand(webClient, requestDto, serviceProperties.getAccessToken(tokenService)).call();
    }

    @Timed(name = "providers", tags = {"operation", "kontoregister_hentKonto"})
    public HentKontoResponseDTO sendHentKontoRequest(String ident) {
        var requestDto = new HentKontoRequestDTO(ident, false);

        return new SendHentKontoregisterCommand(webClient, requestDto, serviceProperties.getAccessToken(tokenService)).call();
    }
}
