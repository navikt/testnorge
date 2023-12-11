package no.nav.registre.sdforvalter.consumer.rs.person;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.config.Consumers;
import no.nav.registre.sdforvalter.domain.person.Person;
import no.nav.testnav.libs.commands.GetPersonCommand;
import no.nav.testnav.libs.dto.person.v1.Persondatasystem;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class PersonConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final Executor executor;

    public PersonConsumer(
            ObjectMapper objectMapper,
            Consumers consumers,
            TokenExchange tokenExchange) {
        serverProperties = consumers.getPerson();
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
        this.executor = Executors.newFixedThreadPool(serverProperties.getThreads());
    }

    private CompletableFuture<Person> hentPerson(String ident, AccessToken accessToken) {
        var command = new GetPersonCommand(webClient, ident, accessToken.getTokenValue(), Persondatasystem.PDL, null);
        return CompletableFuture.supplyAsync(
                command::call,
                executor
        ).thenApply(Person::new);
    }

    public List<Person> hentPersoner(Set<String> identer) {
        AccessToken accessToken = tokenExchange.exchange(serverProperties).block();
        List<Person> personer = new ArrayList<>();
        var futures = identer.stream().map(ident -> hentPerson(ident, accessToken)).toList();
        for (CompletableFuture<Person> future : futures) {
            try {
                Person person = future.get();
                personer.add(person != null && person.getFnr() != null ? person : null);
            } catch (Exception e) {
                log.error("Klarte ikke å hente ut person", e);
                Thread.currentThread().interrupt();
            }
        }
        return personer;
    }
}