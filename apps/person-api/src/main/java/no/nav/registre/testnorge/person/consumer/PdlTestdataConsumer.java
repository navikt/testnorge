package no.nav.registre.testnorge.person.consumer;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
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

    public String createPerson(Person person, String kilde) {
        String token = tokenService.getIdToken();
        log.info("Oppretter person med ident {} i PDL", person.getIdent());

        List<Callable<? extends Object>> commands = new ArrayList<>();

        commands.add(new PostOpprettPersonCommand(restTemplate, pdlTestdataUrl, person.getIdent(), kilde, token));

        if (person.getFornavn() != null && person.getEtternavn() != null) {
            commands.add(new PostNavnCommand(restTemplate, pdlTestdataUrl, person, kilde, token));
        }
        if (person.getAdresse() != null) {
            commands.add(new PostAdresseCommand(restTemplate, pdlTestdataUrl, person, kilde, token));
        }

        for (var command : commands) {
            try {
                command.call();
            } catch (Exception e) {
                throw new PdlCreatePersonException("Feil ved innsendelse til PDL testdata", e);
            }
        }

        return person.getIdent();
    }
}
