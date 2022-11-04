package no.nav.testnav.apps.personservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.personservice.consumer.command.GetPdlAktoerCommand;
import no.nav.testnav.apps.personservice.consumer.command.GetPdlPersonCommand;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql.PdlAktoer;
import no.nav.testnav.apps.personservice.credentials.PdlServiceProperties;
import no.nav.testnav.apps.personservice.domain.Person;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
public class PdlApiConsumer {

    private static final String PDL_URL = "/pdl-api";
    private static final String PDL_Q1_URL = "/pdl-api-q1";

    private final WebClient webClient;
    private final PdlServiceProperties serviceProperties;
    private final TokenExchange tokenExchange;

    public PdlApiConsumer(
            PdlServiceProperties serviceProperties,
            TokenExchange tokenExchange,
            ObjectMapper objectMapper,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
        ExchangeStrategies jacksonStrategy = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    config.defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                })
                .build();

        this.webClient = WebClient
                .builder()
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(serviceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public Mono<Optional<Person>> getPerson(String ident) {

        log.info("Henter person {} fra PDL", ident);
        return tokenExchange
                .exchange(serviceProperties)
                .flatMap(token -> new GetPdlPersonCommand(webClient, PDL_URL, ident, token.getTokenValue()).call())
                .map(pdlPerson -> {
                    if (pdlPerson.getErrors().stream().anyMatch(value -> value.getMessage().equals("Fant ikke person"))) {
                        return Optional.empty();
                    }
                    return Optional.of(new Person(pdlPerson));
                });
    }

    private boolean isNotPresent(PdlAktoer pdlAktoer) {

        return pdlAktoer.getErrors().stream().anyMatch(value -> value.getMessage().equals("Fant ikke person"));
    }

    private boolean isGruppe(PdlAktoer.AktoerIdent ident, String gruppe) {

        return isNotBlank(ident.getIdent()) && !ident.getHistorisk() && gruppe.equals(ident.getGruppe());
    }

    private boolean isPresent(PdlAktoer pdlAktoer) {

        List<PdlAktoer.AktoerIdent> identer = nonNull(pdlAktoer) && nonNull(pdlAktoer.getData()) && nonNull(pdlAktoer.getData().getHentIdenter()) ?
                pdlAktoer.getData().getHentIdenter().getIdenter() : emptyList();

        return nonNull(pdlAktoer) &&
                pdlAktoer.getErrors().stream().noneMatch(value -> value.getMessage().equals("Fant ikke person")) &&
                identer.stream()
                        .filter(ident -> identer.stream().anyMatch(ident2 -> isGruppe(ident2, "AKTORID")))
                        .anyMatch(ident -> identer.stream().anyMatch(ident2 -> isGruppe(ident2, "FOLKEREGISTERIDENT")) ||
                                identer.stream().anyMatch(ident2 -> isGruppe(ident2, "NPID")));
    }

    public Mono<Optional<PdlAktoer.AktoerIdent>> getAktoer(String ident) {

        log.info("Henter ident {} fra PDL", ident);
        return tokenExchange
                .exchange(serviceProperties)
                .flatMap(token -> Mono.zip(new GetPdlAktoerCommand(webClient, PDL_URL, ident, token.getTokenValue()).call(),
                                new GetPdlAktoerCommand(webClient, PDL_Q1_URL, ident, token.getTokenValue()).call())
                        .map(tuple -> {
                            if (isNotPresent(tuple.getT1()) || isNotPresent(tuple.getT2())) {
                                return Optional.empty();
                            }
                            return Optional.of(tuple.getT1().getData().getHentIdenter().getIdenter().get(0));
                        }));
    }

    public Mono<Boolean> isPerson(String ident) {

        log.info("Henter ident {} fra PDL", ident);
        return tokenExchange
                .exchange(serviceProperties)
                .flatMap(token -> Mono.zip(new GetPdlAktoerCommand(webClient, PDL_URL, ident, token.getTokenValue()).call(),
                                new GetPdlAktoerCommand(webClient, PDL_Q1_URL, ident, token.getTokenValue()).call())
                        .map(tuple -> isPresent(tuple.getT1()) && isPresent(tuple.getT2())));
    }
}
