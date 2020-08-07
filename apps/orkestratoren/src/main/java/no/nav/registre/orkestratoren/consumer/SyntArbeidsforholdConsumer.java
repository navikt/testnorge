package no.nav.registre.orkestratoren.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import no.nav.registre.orkestratoren.consumer.command.CreateSyntArbeidsforholdCommand;

@Slf4j
@Component
public class SyntArbeidsforholdConsumer {
    private final RestTemplate restTemplate;
    private final String url;
    private final Executor executor;

    public SyntArbeidsforholdConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.syntarbeidsforhold.url}") String url,
            @Value("${consumers.syntarbeidsforhold.threads}") Integer threads
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url;
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public CompletableFuture<Void> createArbeidsforhold(String ident, LocalDate foedselsdato) {
        log.info("Oppretter synt arbeidsforhold for {}.", ident);
        var command = new CreateSyntArbeidsforholdCommand(restTemplate, url, ident, foedselsdato);
        return CompletableFuture.supplyAsync(command::call, executor);
    }
}
