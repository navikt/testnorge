package no.nav.registre.testnorge.profil.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.profil.config.Consumers;
import no.nav.registre.testnorge.profil.consumer.command.GetPersonOrganisasjonTilgangCommand;
import no.nav.registre.testnorge.profil.consumer.dto.OrganisasjonDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class PersonOrganisasjonTilgangConsumer {
    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public PersonOrganisasjonTilgangConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            ObjectMapper objectMapper
    ) {
        serverProperties = consumers.getTestnavPersonOrganisasjonTilgangService();
        this.tokenExchange = tokenExchange;
        ExchangeStrategies jacksonStrategy = ExchangeStrategies
                .builder()
                .codecs(
                        config -> {
                            config
                                    .defaultCodecs()
                                    .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                            config
                                    .defaultCodecs()
                                    .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                        })
                .build();
        this.webClient = WebClient
                .builder()
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<OrganisasjonDTO> getOrganisasjon(String organisasjonsnummer) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new GetPersonOrganisasjonTilgangCommand(webClient, accessToken.getTokenValue(), organisasjonsnummer).call())
                .onErrorResume(
                        WebClientResponseException.class::isInstance,
                        throwable -> {
                            log.warn("Person har ikke tilgang til organisasjon {}.", organisasjonsnummer);
                            return Mono.empty();
                        });
    }
}
