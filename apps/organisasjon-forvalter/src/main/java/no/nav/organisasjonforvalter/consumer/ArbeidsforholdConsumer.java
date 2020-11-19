package no.nav.registre.orkestratoren.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import no.nav.registre.orkestratoren.consumer.command.GetArbeidsforholdCommand;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v1.ArbeidsforholdDTO;


@Slf4j
@Component
public class ArbeidsforholdConsumer {
    private final RestTemplate restTemplate;
    private final String url;
    private final Executor executor;

    public ArbeidsforholdConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.arbeidsforhold.url}") String url,
            @Value("${consumers.arbeidsforhold.threads}") Integer threads
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url;
        this.executor = Executors.newFixedThreadPool(threads);
    }

    @SneakyThrows
    public CompletableFuture<ArbeidsforholdDTO> getArbeidsforholdAt(String ident, LocalDate date) {
        var command = new GetArbeidsforholdCommand(restTemplate, url, ident);
        log.info("Henter arbeidsforhold for {} på dato {}.", ident, date);
        return CompletableFuture
                .supplyAsync(command::call, executor)
                .thenApply(list -> list
                        .stream()
                        .filter(value -> value.getFom().isBefore(date) || value.getFom().isEqual(date))
                        .filter(value -> value.getTom() == null || value.getTom().isEqual(date) || value.getTom().isAfter(date))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Klarer ikke å finne aktivt arbeidsforhold for " + ident))
                );
    }


}
