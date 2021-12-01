package no.nav.testnav.apps.personservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

import no.nav.testnav.apps.personservice.consumer.command.GetPdlAktoerCommand;
import no.nav.testnav.apps.personservice.consumer.command.GetPdlPersonCommand;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql.PdlAktoer;
import no.nav.testnav.apps.personservice.credentials.PdlServiceProperties;
import no.nav.testnav.apps.personservice.domain.Person;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;

@Slf4j
@Component
public class PdlApiConsumer {

    private final WebClient webClient;
    private final PdlServiceProperties serviceProperties;
    private final TokenExchange tokenExchange;

    public PdlApiConsumer(
            PdlServiceProperties serviceProperties,
            TokenExchange tokenExchange,
            ObjectMapper objectMapper
    ) {
        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
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
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    public Mono<Optional<Person>> getPerson(String ident) {
        log.info("Henter person {} fra PDL", ident);
        return tokenExchange
                .exchange(serviceProperties)
                .flatMap(token -> new GetPdlPersonCommand(webClient, ident, token.getTokenValue()).call())
                .map(pdlPerson -> {
                    if (pdlPerson.getErrors().stream().anyMatch(value -> value.getMessage().equals("Fant ikke person"))) {
                        return Optional.empty();
                    }
                    return Optional.of(new Person(pdlPerson));
                });
    }

    public Mono<Optional<PdlAktoer.AktoerIdent>> getAktoer(String ident) {
        log.info("Henter ident {} fra PDL", ident);
        return tokenExchange
                .exchange(serviceProperties)
                .flatMap(token -> new GetPdlAktoerCommand(webClient, ident, token.getTokenValue()).call())
                .map(pdlAktoer -> {
                    if (pdlAktoer.getErrors().stream().anyMatch(value -> value.getMessage().equals("Fant ikke person"))) {
                        return Optional.empty();
                    }
                    return Optional.of(pdlAktoer.getData().getHentIdenter().getIdenter().get(0));
                });

    }
}
