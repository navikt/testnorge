package no.nav.registre.testnorge.person.consumer;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.person.consumer.command.PostAdresseCommand;
import no.nav.registre.testnorge.person.consumer.command.PostNavnCommand;
import no.nav.registre.testnorge.person.consumer.command.PostOpprettPersonCommand;
import no.nav.registre.testnorge.person.domain.Person;
import no.nav.registre.testnorge.person.service.StsOidcTokenService;

@Slf4j
@Component
@DependencyOn(value = "pdl-testdata", external = true)
public class PdlTestdataConsumer {

    private final StsOidcTokenService tokenService;
    private final String url;
    private final RestTemplate restTemplate;
    private final Executor executor;

    public PdlTestdataConsumer(
            StsOidcTokenService tokenService,
            @Value("${system.pdl.url}") String url,
            @Value("${system.pdl.threads}") Integer threads,
            RestTemplateBuilder restTemplateBuilder
    ) {
        this.tokenService = tokenService;
        this.url = url;
        this.restTemplate = restTemplateBuilder.build();
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public void createPerson(Person person) {
        String token = tokenService.getIdToken();
        log.info("Oppretter person med ident {} i PDL", person.getIdent());

        Arrays.asList(
                new PostOpprettPersonCommand(restTemplate, url, person.getIdent(), token),
                new PostNavnCommand(restTemplate, url, person, token),
                new PostAdresseCommand(restTemplate, url, person, token)
        ).forEach(callable -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return callable.call();
                    } catch (Exception e) {
                        log.error("Klarer ikke å utfløre kall til PDL", e);
                        return null;
                    }
                }, executor
        ));
    }
}
