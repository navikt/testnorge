package no.nav.testnav.endringsmeldingservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.testnav.endringsmeldingservice.config.Consumers;
import no.nav.testnav.endringsmeldingservice.consumer.command.SendDoedsmeldingOldCommand;
import no.nav.testnav.endringsmeldingservice.consumer.command.SendFoedselsmeldingOldCommand;
import no.nav.testnav.endringsmeldingservice.consumer.dto.DoedsmeldingDTO;
import no.nav.testnav.endringsmeldingservice.consumer.request.FoedselsmeldingRequest;
import no.nav.testnav.endringsmeldingservice.domain.Status;
import no.nav.testnav.libs.dto.endringsmelding.v1.FoedselsmeldingDTO;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
public class TpsForvalterConsumer {
    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange accessTokenService;

    public TpsForvalterConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            ObjectMapper objectMapper) {

        serverProperties = consumers.getTpsForvalterenProxy();
        this.accessTokenService = tokenExchange;

        ExchangeStrategies jacksonStrategy = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    config.defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                }).build();


        this.webClient = WebClient
                .builder()
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<Status> sendFoedselsmelding(FoedselsmeldingDTO dto, Set<String> miljoer) {
        return accessTokenService
                .exchange(serverProperties)
                .flatMap(accessToken -> new SendFoedselsmeldingOldCommand(webClient, new FoedselsmeldingRequest(dto, miljoer), accessToken.getTokenValue()).call())
                .map(Status::new);
    }

    public Mono<Status> sendDoedsmelding(no.nav.testnav.libs.dto.endringsmelding.v1.DoedsmeldingDTO dto, Set<String> miljoer) {
        return accessTokenService
                .exchange(serverProperties)
                .flatMap(accessToken -> new SendDoedsmeldingOldCommand(webClient, new DoedsmeldingDTO(dto, miljoer), accessToken.getTokenValue()).call())
                .map(Status::new);
    }
}
