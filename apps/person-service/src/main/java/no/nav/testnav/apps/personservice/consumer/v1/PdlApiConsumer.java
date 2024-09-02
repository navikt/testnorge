package no.nav.testnav.apps.personservice.consumer.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.personservice.config.Consumers;
import no.nav.testnav.apps.personservice.consumer.v1.command.GetPdlAktoerCommand;
import no.nav.testnav.apps.personservice.consumer.v1.command.GetPdlPersonCommand;
import no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql.MetadataDTO;
import no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql.PdlAktoer;
import no.nav.testnav.apps.personservice.domain.Person;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
public class PdlApiConsumer {

    private static final String PDL_URL = "/pdl-api";
    private static final String PDL_Q1_URL = "/pdl-api-q1";

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public PdlApiConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            ObjectMapper objectMapper
    ) {
        serverProperties = consumers.getPdlProxy();
        this.tokenExchange = tokenExchange;
        ExchangeStrategies jacksonStrategy = ExchangeStrategies.builder()
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

    public Mono<Optional<Person>> getPerson(String ident) {

        log.info("Henter person {} fra PDL", ident);
        return tokenExchange
                .exchange(serverProperties)
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

    private boolean isPresent(String ident, Tuple2<PdlAktoer, PdlAktoer> pdlAktoer, Set<String> opplysningId) {

        var statusQ1 = isPresent(ident, pdlAktoer.getT1(), "q1", opplysningId);
        var statusQ2 = isPresent(ident, pdlAktoer.getT2(), "q2", opplysningId);

        log.info("Ident {}, isPresent() {}, (q1: {}, q2: {})", ident, statusQ1 && statusQ2, statusQ1, statusQ2);
        return statusQ1 && statusQ2;
    }

    private boolean isPresent(String ident, PdlAktoer pdlAktoer, String miljoe, Set<String> opplysningId) {

        var person = pdlAktoer.getData().getHentPerson();
        log.info("Sjekker ident {} i miljø {}, med PDL opplysningId {}, sjekkes for mottatt opplysningId {}", ident, miljoe,
                nonNull(person) ?
                        Stream.of(person.getNavn(), person.getFoedselsdato(), person.getKjoenn(), person.getFolkeregisterpersonstatus())
                                .flatMap(Collection::stream)
                                .map(MetadataDTO::getMetadata)
                                .map(MetadataDTO.Metadata::getOpplysningsId)
                                .collect(Collectors.joining(", ")) : null,
                nonNull(opplysningId) ?
                        String.join(", ", opplysningId) :
                        null);

        boolean resultat;
        if (nonNull(opplysningId)) {

            resultat = nonNull(person) &&
                    Stream.of(person.getNavn(), person.getFoedselsdato(), person.getKjoenn(), person.getFolkeregisterpersonstatus())
                            .flatMap(Collection::stream)
                            .map(MetadataDTO::getMetadata)
                            .map(MetadataDTO.Metadata::getOpplysningsId)
                            .anyMatch(opplysningId::contains);

        } else {

            List<PdlAktoer.AktoerIdent> identer = nonNull(pdlAktoer) && nonNull(pdlAktoer.getData()) && nonNull(pdlAktoer.getData().getHentIdenter()) ?
                    pdlAktoer.getData().getHentIdenter().getIdenter() : emptyList();

            resultat = nonNull(pdlAktoer) &&
                    pdlAktoer.getErrors().stream().noneMatch(value -> value.getMessage().equals("Fant ikke person")) &&
                    identer.stream()
                            .filter(ident1 -> identer.stream().anyMatch(ident2 -> isGruppe(ident2, "AKTORID")))
                            .anyMatch(ident1 -> identer.stream().anyMatch(ident2 -> isGruppe(ident2, "FOLKEREGISTERIDENT")) ||
                                    identer.stream().anyMatch(ident2 -> isGruppe(ident2, "NPID")));
        }

        return resultat;
    }

    public Mono<Optional<PdlAktoer.AktoerIdent>> getAktoer(String ident) {

        log.info("Henter ident {} fra PDL", ident);
        return tokenExchange
                .exchange(serverProperties)
                .flatMap(token -> Mono.zip(new GetPdlAktoerCommand(webClient, PDL_URL, ident, token.getTokenValue()).call(),
                                new GetPdlAktoerCommand(webClient, PDL_Q1_URL, ident, token.getTokenValue()).call())
                        .map(tuple -> {
                            if (isNotPresent(tuple.getT1()) || isNotPresent(tuple.getT2())) {
                                return Optional.empty();
                            }
                            return Optional.of(tuple.getT1().getData().getHentIdenter().getIdenter().get(0));
                        }));
    }

    public Mono<Boolean> isPerson(String ident, Set<String> opplysningId) {

        return tokenExchange
                .exchange(serverProperties)
                .flatMap(token -> Mono.zip(new GetPdlAktoerCommand(webClient, PDL_Q1_URL, ident, token.getTokenValue()).call(),
                                new GetPdlAktoerCommand(webClient, PDL_URL, ident, token.getTokenValue()).call())
                        .map(tuple -> isPresent(ident, tuple, opplysningId)));
    }
}
