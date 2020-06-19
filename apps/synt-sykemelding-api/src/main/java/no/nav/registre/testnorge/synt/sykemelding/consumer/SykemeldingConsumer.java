package no.nav.registre.testnorge.synt.sykemelding.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.synt.sykemelding.consumer.command.PostSykemeldingCommand;
import no.nav.registre.testnorge.synt.sykemelding.domain.Sykemelding;
import no.nav.registre.testnorge.synt.sykemelding.domain.SykemeldingListe;

@Slf4j
@Component
@DependencyOn("sykemelding-api")
public class SykemeldingConsumer {
    private final RestTemplate restTemplate;
    private final String url;
    private final Executor executor;

    public SykemeldingConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.sykemelding.url}") String url,
            @Value("${consumers.sykemelding.threads}") Integer threads
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url;
        this.executor = Executors.newFixedThreadPool(threads);
    }


    private CompletableFuture<ResponseEntity<String>> opprettSykemelding(Sykemelding sykemelding) {
        return CompletableFuture.supplyAsync(
                () -> new PostSykemeldingCommand(restTemplate, url, sykemelding).call(),
                executor
        );
    }


    public void opprett(SykemeldingListe liste) {
        var futures = liste.getListe().stream().map(this::opprettSykemelding).collect(Collectors.toList());
        futures.forEach(future -> {
            try {
                future.get();
            } catch (Exception e) {
                log.error("Klerer ikke å opprette alle sykemeldingnene", e);
            }
        });
    }
}
