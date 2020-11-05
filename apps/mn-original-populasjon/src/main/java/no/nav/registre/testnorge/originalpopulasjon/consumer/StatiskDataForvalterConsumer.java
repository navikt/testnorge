package no.nav.registre.testnorge.originalpopulasjon.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.person.v1.PersonDTO;
import no.nav.registre.testnorge.originalpopulasjon.consumer.command.GetFasteDataIdenterCommand;

@Slf4j
@Component
public class StatiskDataForvalterConsumer {

    private final WebClient webClient;
    private final Executor executor;

    public StatiskDataForvalterConsumer(
            @Value("${consumer.statisk-data-forvalter.url}") String url,
            @Value("${consumer.statisk-data-forvalter.threads}") Integer threads
    ) {
        this.executor = Executors.newFixedThreadPool(threads);
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }

    private CompletableFuture<PersonDTO> getPerson(String ident) {
        return CompletableFuture.supplyAsync(
                () -> new GetFasteDataIdenterCommand(webClient, ident).call(),
                executor
        );
    }

    public Set<String> getPersoner(List<String> identer) {
        HashSet<String> personer = new HashSet<>();

        var futures = identer
                .stream()
                .map(this::getPerson)
                .collect(Collectors.toList());

        for (CompletableFuture<PersonDTO> future : futures) {
            try {
                var person = future.get();
                if (person != null) {
                    personer.add(person.getIdent());
                }
            } catch (Exception e) {
                log.error("Klarte ikke å hente person", e);
            }
        }
        return personer;
    }
}
