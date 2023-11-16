package no.nav.dolly.bestilling.tpsmessagingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.tpsmessagingservice.command.EgenansattDeleteCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.EgenansattPostCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.PersonGetCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.SikkerhetstiltakDeleteCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.TelefonnummerDeleteCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.TpsMessagingPostCommand;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.data.kontoregister.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.data.kontoregister.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonMiljoeDTO;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.SpraakDTO;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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
    private static final List<String> TELEFONTYPER_LISTE = Arrays.asList("ARBT", "HJET", "MOBI");

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public TpsMessagingConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavTpsMessagingService();
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create(ConnectionProvider.builder("custom")
                                        .maxConnections(5)
                                        .pendingAcquireMaxCount(500)
                                        .pendingAcquireTimeout(Duration.ofMinutes(15))
                                        .build())
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createUtenlandskBankkonto"})
    public Flux<TpsMeldingResponseDTO> sendUtenlandskBankkontoRequest(String ident, List<String> miljoer,
                                                                      BankkontonrUtlandDTO body) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token ->
                        new TpsMessagingPostCommand(webClient, ident, miljoer, body, UTENLANDSK_BANKKONTO_URL, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createNorskBankkonto"})
    public Flux<TpsMeldingResponseDTO> sendNorskBankkontoRequest(String ident, List<String> miljoer, BankkontonrNorskDTO body) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token ->
                        new TpsMessagingPostCommand(webClient, ident, miljoer, body, NORSK_BANKKONTO_URL, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_deleteSikkerhetstiltak"})
    public Flux<TpsMeldingResponseDTO> deleteSikkerhetstiltakRequest(String ident, List<String> miljoer) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new SikkerhetstiltakDeleteCommand(webClient, ident, miljoer, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createSikkerhetstiltak"})
    public Flux<TpsMeldingResponseDTO> sendSikkerhetstiltakRequest(String ident, List<String> miljoer, Object body) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token ->
                        new TpsMessagingPostCommand(webClient, ident, miljoer, body, SIKKERHETSTILTAK_URL, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createSkjerming"})
    public Flux<TpsMeldingResponseDTO> sendEgenansattRequest(String ident, List<String> miljoer, LocalDate fraOgMed) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token ->
                        new EgenansattPostCommand(webClient, ident, miljoer, fraOgMed, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_deleteSkjerming"})
    public Flux<TpsMeldingResponseDTO> deleteEgenansattRequest(String ident, List<String> miljoer) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new EgenansattDeleteCommand(webClient, ident, miljoer, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createTelefonnummer"})
    public Flux<TpsMeldingResponseDTO> sendTelefonnummerRequest(String ident, List<String> miljoer, Object body) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token ->
                        new TpsMessagingPostCommand(webClient, ident, miljoer, body, TELEFONNUMMER_URL, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_deleteTelefonnummer"})
    public Flux<TpsMeldingResponseDTO> deleteTelefonnummerRequest(String ident, List<String> miljoer) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token ->
                        new TelefonnummerDeleteCommand(webClient, ident, miljoer, TELEFONTYPER_LISTE, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_createSpraakkode"})
    public Flux<TpsMeldingResponseDTO> sendSpraakkodeRequest(String ident, List<String> miljoer, SpraakDTO body) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token ->
                        new TpsMessagingPostCommand(webClient, ident, miljoer, body, SPRAAKKODE_URL, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_getPersoner"})
    public Flux<PersonMiljoeDTO> getPersoner(List<String> identer, List<String> miljoer) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .flatMap(index -> new PersonGetCommand(webClient, identer.get(index), miljoer, token.getTokenValue()).call()));
    }

    @Timed(name = "providers", tags = {"operation", "tps_messaging_getPerson"})
    public Flux<PersonMiljoeDTO> getPerson(String ident, List<String> miljoer) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new PersonGetCommand(webClient, ident, miljoer, token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-tps-messaging-service";
    }

}
