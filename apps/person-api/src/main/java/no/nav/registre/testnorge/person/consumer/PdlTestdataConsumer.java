package no.nav.registre.testnorge.person.consumer;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.person.consumer.command.GetTagsCommand;
import no.nav.registre.testnorge.person.consumer.command.PostAdresseCommand;
import no.nav.registre.testnorge.person.consumer.command.PostNavnCommand;
import no.nav.registre.testnorge.person.consumer.command.PostOpprettPersonCommand;
import no.nav.registre.testnorge.person.consumer.command.PostTagsCommand;
import no.nav.registre.testnorge.person.domain.Person;
import no.nav.registre.testnorge.person.exception.PdlCreatePersonException;
import no.nav.registre.testnorge.person.service.StsOidcTokenService;

@Slf4j
@Component
@DependencyOn(value = "pdl-testdata", external = true)
public class PdlTestdataConsumer {

    private final StsOidcTokenService tokenService;
    private final WebClient webClient;

    public PdlTestdataConsumer(
            StsOidcTokenService tokenService,
            @Value("${system.pdl.pdlTestdataUrl}") String pdlTestdataUrl
    ) {
        this.tokenService = tokenService;
        this.webClient = WebClient.builder()
                .baseUrl(pdlTestdataUrl)
                .build();
    }

    public String createPerson(Person person, String kilde) {
        String token = tokenService.getIdToken();
        log.info("Oppretter person med ident {} i PDL", person.getIdent());

        List<Callable<? extends Object>> commands = new ArrayList<>();

        commands.add(new PostOpprettPersonCommand(webClient, person.getIdent(), kilde, token));

        if (person.getFornavn() != null && person.getEtternavn() != null) {
            commands.add(new PostNavnCommand(webClient, person, kilde, token));
        }

        if (person.getAdresse() != null) {
            commands.add(new PostAdresseCommand(webClient, person, kilde, token));
        }

        if (person.getTags() != null && !person.getTags().isEmpty()) {
            new PostTagsCommand(webClient, person, token).call();
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

    public Set<String> getTags (String ident) {
        String token = tokenService.getIdToken();
        return new GetTagsCommand(webClient, ident, token).call();
    }
}
