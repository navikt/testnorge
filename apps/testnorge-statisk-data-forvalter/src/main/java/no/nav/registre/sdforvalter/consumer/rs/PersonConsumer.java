package no.nav.registre.sdforvalter.consumer.rs;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.sdforvalter.consumer.rs.command.PostPersonCommand;
import no.nav.registre.sdforvalter.domain.TpsIdent;
import no.nav.registre.sdforvalter.domain.TpsIdentListe;
import no.nav.registre.sdforvalter.exception.UgyldigIdentException;
import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;

@Slf4j
@Component
@DependencyOn("person-api")
public class PersonConsumer {

    private final RestTemplate restTemplate;
    private final String url;
    private final Executor executor;

    public PersonConsumer(
            RestTemplate restTemplate,
            @Value("${person.api.url}") String url,
            @Value("${person.api.threads}") Integer threads
    ) {
        this.restTemplate = restTemplate;
        this.url = url;
        this.executor = Executors.newFixedThreadPool(threads);
    }

    private CompletableFuture<ResponseEntity<String>> send(TpsIdent tpsIdent) {
        return CompletableFuture.supplyAsync(
                () -> new PostPersonCommand(restTemplate, url, tpsIdent).call(),
                executor
        );
    }

    public void send(TpsIdentListe liste) {
        List<CompletableFuture<ResponseEntity<String>>> futures = liste
                .getListe()
                .stream()
                .map(this::send)
                .collect(Collectors.toList());

        List<ResponseEntity<String>> responses = futures.stream().map(value -> {
            try {
                return value.get();
            } catch (Exception e) {
                log.error("Feil ved innsende til person API", e);
                return null;
            }
        }).collect(Collectors.toList());

        if (responses.stream().anyMatch(Objects::isNull)) {
            throw new UgyldigIdentException("Klarer ikke å opprette alle identer");
        }
        if (responses.stream().map(value -> !value.getStatusCode().is2xxSuccessful()).findAny().isEmpty()) {
            throw new UgyldigIdentException("Klarer ikke å opprette alle identer");
        }
    }
}
