package no.nav.dolly.bestilling.tpsmessagingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.tpsmessagingservice.command.EgenansattDeleteCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.EgenansattPostCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.PersonGetCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.SikkerhetstiltakDeleteCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.TelefonnummerDeleteCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.TpsMessagingPostCommand;
import no.nav.dolly.config.credentials.TpsMessagingServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonMiljoeDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SpraakDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class TpsMessagingConsumer implements ConsumerStatus {

    private static final String BASE_URL = "/api/v1/personer/{ident}";
    private static final String UTENLANDSK_BANKKONTO_URL = BASE_URL + "/bankkonto-utenlandsk";
    private static final String NORSK_BANKKONTO_URL = BASE_URL + "/bankkonto-norsk";
    private static final String SIKKERHETSTILTAK_URL = BASE_URL + "/sikkerhetstiltak";
    private static final String SPRAAKKODE_URL = BASE_URL + "/spraakkode";
    private static final String TELEFONNUMMER_URL = BASE_URL + "/telefonnumre";
    private static final String ADRESSE_UTLAND_URL = BASE_URL + "/adresse-utland";

    private static final List<String> TELEFONTYPER_LISTE = Arrays.asList("ARBT", "HJET", "MOBI");

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;

    public TpsMessagingConsumer(TokenExchange tokenService,
                                TpsMessagingServiceProperties serverProperties,
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

    public Mono<AccessToken> getToken() {

        return tokenService.exchange(serviceProperties);
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createUtenlandskBankkonto"})
    public Flux<TpsMeldingResponseDTO> sendUtenlandskBankkontoRequest(String ident, List<String> miljoer,
                                                                      BankkontonrUtlandDTO body, AccessToken token) {

        return new TpsMessagingPostCommand(webClient, ident, miljoer, body, UTENLANDSK_BANKKONTO_URL, token.getTokenValue()).call();
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createNorskBankkonto"})
    public Flux<TpsMeldingResponseDTO> sendNorskBankkontoRequest(String ident, List<String> miljoer, BankkontonrNorskDTO body, AccessToken token) {

        return new TpsMessagingPostCommand(webClient, ident, miljoer, body, NORSK_BANKKONTO_URL, token.getTokenValue()).call();
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_deleteSikkerhetstiltak"})
    public Flux<TpsMeldingResponseDTO> deleteSikkerhetstiltakRequest(String ident, List<String> miljoer, AccessToken token) {

        return new SikkerhetstiltakDeleteCommand(webClient, ident, miljoer, token.getTokenValue()).call();
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createSikkerhetstiltak"})
    public Flux<TpsMeldingResponseDTO> sendSikkerhetstiltakRequest(String ident, List<String> miljoer, Object body, AccessToken token) {

        return new TpsMessagingPostCommand(webClient, ident, miljoer, body, SIKKERHETSTILTAK_URL, token.getTokenValue()).call();
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createSkjerming"})
    public Flux<TpsMeldingResponseDTO> sendEgenansattRequest(String ident, List<String> miljoer, LocalDate fraOgMed, AccessToken token) {

        return new EgenansattPostCommand(webClient, ident, miljoer, fraOgMed, token.getTokenValue()).call();
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_deleteSkjerming"})
    public Flux<TpsMeldingResponseDTO> deleteEgenansattRequest(String ident, List<String> miljoer, AccessToken token) {

        return new EgenansattDeleteCommand(webClient, ident, miljoer, token.getTokenValue()).call();
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createTelefonnummer"})
    public Flux<TpsMeldingResponseDTO> sendTelefonnummerRequest(String ident, List<String> miljoer, Object body, AccessToken token) {

        return new TpsMessagingPostCommand(webClient, ident, miljoer, body, TELEFONNUMMER_URL, token.getTokenValue()).call();
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createAdresseUtland"})
    public Flux<TpsMeldingResponseDTO> sendAdresseUtlandRequest(String ident, List<String> miljoer, Object body, AccessToken token) {

        return new TpsMessagingPostCommand(webClient, ident, miljoer, body, ADRESSE_UTLAND_URL, token.getTokenValue()).call();
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_deleteTelefonnummer"})
    public Flux<TpsMeldingResponseDTO> deleteTelefonnummerRequest(String ident, List<String> miljoer, AccessToken token) {

        return new TelefonnummerDeleteCommand(webClient, ident, miljoer, TELEFONTYPER_LISTE, token.getTokenValue()).call();
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createSpraakkode"})
    public Flux<TpsMeldingResponseDTO> sendSpraakkodeRequest(String ident, List<String> miljoer, SpraakDTO body, AccessToken token) {

        return new TpsMessagingPostCommand(webClient, ident, miljoer, body, SPRAAKKODE_URL, token.getTokenValue()).call();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_getPersoner"})
    public Flux<PersonMiljoeDTO> getPersoner(List<String> identer, List<String> miljoer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .flatMap(index -> new PersonGetCommand(webClient, identer.get(index), miljoer, token.getTokenValue()).call()));
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_getPerson"})
    public Flux<PersonMiljoeDTO> getPerson(String ident, List<String> miljoer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new PersonGetCommand(webClient, ident, miljoer, token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-tps-messaging-service";
    }

}
