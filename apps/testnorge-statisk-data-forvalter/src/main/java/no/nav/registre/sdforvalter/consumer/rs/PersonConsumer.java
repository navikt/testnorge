package no.nav.registre.sdforvalter.consumer.rs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.config.credentials.PersonServiceProperties;
import no.nav.registre.sdforvalter.domain.TpsIdent;
import no.nav.registre.sdforvalter.domain.TpsIdentListe;
import no.nav.registre.sdforvalter.domain.person.Person;
import no.nav.registre.sdforvalter.exception.UgyldigIdentException;
import no.nav.testnav.libs.commands.CreatePersonCommand;
import no.nav.testnav.libs.commands.GetPersonCommand;
import no.nav.testnav.libs.dto.person.v1.Persondatasystem;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class PersonConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final Executor executor;

    public PersonConsumer(
            ObjectMapper objectMapper, @Value("${consumers.person.threads}") Integer threads,
            PersonServiceProperties personServiceProperties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = personServiceProperties;
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
                .baseUrl(personServiceProperties.getUrl())
                .build();
        this.executor = Executors.newFixedThreadPool(threads);
    }


    private CompletableFuture<Person> hentPerson(String ident, AccessToken accessToken) {
        var command = new GetPersonCommand(webClient, ident, accessToken.getTokenValue(), Persondatasystem.PDL, null);
        return CompletableFuture.supplyAsync(
                command::call,
                executor
        ).thenApply(Person::new);
    }

    public List<Person> hentPersoner(Set<String> identer) {
        AccessToken accessToken = tokenExchange.generateToken(serviceProperties).block();
        List<Person> personer = new ArrayList<>();
        var futures = identer.stream().map(ident -> hentPerson(ident, accessToken)).collect(Collectors.toList());
        for (CompletableFuture<Person> future : futures) {
            try {
                Person person = future.get();
                personer.add(person != null && person.getFnr() != null ? person : null);
            } catch (Exception e) {
                log.error("Klarte ikke å hente ut person", e);
            }
        }
        return personer;
    }

    public void opprettPersoner(TpsIdentListe identer) {
        AccessToken accessToken = tokenExchange.generateToken(serviceProperties).block();
        List<CompletableFuture<TpsIdent>> futures = identer.stream().map(ident -> CompletableFuture.supplyAsync(() -> {
                    try {
                        new CreatePersonCommand(webClient, ident.toDTO(), accessToken.getTokenValue(), ident.getOpprinnelse()).run();
                        return ident;
                    } catch (Exception e) {
                        log.error("Kunne ikke opprette ident {}", ident.getFnr(), e);
                        return null;
                    }
                }, executor)
        ).collect(Collectors.toList());


        List<TpsIdent> opprettedeIdenter = futures.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Noe gikk galt ved henting av resultat fra tråd", e);
                return null;
            }
        }).collect(Collectors.toList());

        if (opprettedeIdenter.stream().anyMatch(Objects::isNull)) {
            throw new UgyldigIdentException("Klarte ikke å opprette alle identer");
        }
    }
}
