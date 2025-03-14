package no.nav.dolly.bestilling.tpsmessagingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.tpsmessagingservice.command.EgenansattDeleteCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.EgenansattPostCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.PersonHentCommand;
import no.nav.dolly.bestilling.tpsmessagingservice.command.TpsMessagingPostCommand;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.data.kontoregister.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.data.kontoregister.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonMiljoeDTO;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class TpsMessagingConsumer extends ConsumerStatus {

    private static final String BASE_URL = "/api/v1/personer/{ident}";
    private static final String UTENLANDSK_BANKKONTO_URL = BASE_URL + "/bankkonto-utenlandsk";
    private static final String NORSK_BANKKONTO_URL = BASE_URL + "/bankkonto-norsk";

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public TpsMessagingConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient webClient
    ) {
        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavTpsMessagingService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
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

    @Timed(name = "providers", tags = {"operation", "tps_messaging_getPerson"})
    public Flux<PersonMiljoeDTO> getPerson(String ident, List<String> miljoer) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new PersonHentCommand(webClient, ident, miljoer, token.getTokenValue()).call());
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
