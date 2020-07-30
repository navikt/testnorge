package no.nav.registre.testnorge.person.consumer;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.person.consumer.command.PostAdresseCommand;
import no.nav.registre.testnorge.person.consumer.command.PostNavnCommand;
import no.nav.registre.testnorge.person.consumer.command.PostOpprettPersonCommand;
import no.nav.registre.testnorge.person.domain.Person;
import no.nav.registre.testnorge.person.exception.PdlCreatePersonException;
import no.nav.registre.testnorge.person.service.StsOidcTokenService;

@Slf4j
@Component
@DependencyOn(value = "pdl-testdata", external = true)
public class PdlTestdataConsumer {

    private final StsOidcTokenService tokenService;
    private final String pdlTestdataUrl;
    private final RestTemplate restTemplate;
    private final Executor executor;

    public PdlTestdataConsumer(
            StsOidcTokenService tokenService,
            @Value("${system.pdl.pdlTestdataUrl}") String pdlTestdataUrl,
            @Value("${system.pdl.threads}") Integer threads,
            RestTemplateBuilder restTemplateBuilder
    ) {
        this.tokenService = tokenService;
        this.pdlTestdataUrl = pdlTestdataUrl;
        this.restTemplate = restTemplateBuilder.build();
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public void createPerson(Person person) {
        String token = tokenService.getIdToken();
        log.info("Oppretter person med ident {} i PDL", person.getIdent());

        List<? extends CompletableFuture<?>> results = Stream.of(
                new PostOpprettPersonCommand(restTemplate, pdlTestdataUrl, person.getIdent(), token),
                new PostNavnCommand(restTemplate, pdlTestdataUrl, person, token),
                new PostAdresseCommand(restTemplate, pdlTestdataUrl, person, token)
        ).map(callable -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return callable.call();
                    } catch (Exception e) {
                        log.error("Klarer ikke å utfløre kall til PDL", e);
                        return null;
                    }
                }, executor
        )).collect(Collectors.toList());

        if (results.stream().anyMatch(Objects::isNull)) {
            throw new PdlCreatePersonException("Feil ved innsendelse til PDL testdata");
        }
    }
}
