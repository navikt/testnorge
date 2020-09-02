package no.nav.registre.testnorge.person.consumer;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

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

    public PdlTestdataConsumer(
            StsOidcTokenService tokenService,
            @Value("${system.pdl.pdlTestdataUrl}") String pdlTestdataUrl,
            RestTemplateBuilder restTemplateBuilder
    ) {
        this.tokenService = tokenService;
        this.pdlTestdataUrl = pdlTestdataUrl;
        this.restTemplate = restTemplateBuilder.build();
    }

    public String createPerson(Person person) {
        String token = tokenService.getIdToken();
        log.info("Oppretter person med ident {} i PDL", person.getIdent());

        var commaneds = Arrays.asList(
                new PostOpprettPersonCommand(restTemplate, pdlTestdataUrl, person.getIdent(), token),
                new PostNavnCommand(restTemplate, pdlTestdataUrl, person, token),
                new PostAdresseCommand(restTemplate, pdlTestdataUrl, person, token)
        );

        for (var command : commaneds) {
            try {
                command.call();
            } catch (Exception e) {
                log.error("Klarer ikke å utfløre kall til PDL", e);
                throw new PdlCreatePersonException("Feil ved innsendelse til PDL testdata");
            }
        }

        return person.getIdent();
    }
}
