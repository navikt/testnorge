package no.nav.registre.orkestratoren.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.registre.orkestratoren.consumer.command.GetPersonCommand;
import no.nav.registre.orkestratoren.consumer.dto.PersonDTO;
import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;

@Slf4j
@Component
@DependencyOn("person-api")
public class PersonConsumer {

    private final RestTemplate restTemplate;
    private final String url;
    private final Executor executor;

    public PersonConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.person.url}") String url,
            @Value("${consumers.person.threads}") Integer threads
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url;
        this.executor = Executors.newFixedThreadPool(threads);
    }

    private CompletableFuture<PersonDTO> getPerson(String ident) {
        return CompletableFuture.supplyAsync(() -> new GetPersonCommand(restTemplate, url, ident).call(), executor);
    }

    public Set<PersonDTO> getPersoner(Set<String> identer) {
        var futures = identer.stream().map(this::getPerson).collect(Collectors.toList());
        return futures.stream().map(future -> {
            try {
                return future.get();
            } catch (Exception e) {
                log.error("Klarte ikke å hente ut person", e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}