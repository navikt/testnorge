package no.nav.testnav.apps.personservice.consumer.v1;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.personservice.config.Consumers;
import no.nav.testnav.apps.personservice.consumer.v1.command.OpprettFoedselsdatoCommand;
import no.nav.testnav.apps.personservice.consumer.v1.command.OpprettPersonCommand;
import no.nav.testnav.apps.personservice.consumer.v1.command.PostAdresseCommand;
import no.nav.testnav.apps.personservice.consumer.v1.command.PostNavnCommand;
import no.nav.testnav.apps.personservice.consumer.v1.command.PostTagsCommand;
import no.nav.testnav.apps.personservice.consumer.v1.exception.PdlCreatePersonException;
import no.nav.testnav.apps.personservice.domain.Person;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class PdlTestdataConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public PdlTestdataConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getPdlProxy();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public String ordrePerson(Person person, String kilde) {
        log.info("Oppretter person med ident {} i PDL", person.getIdent());

        try {
            var accessToken = tokenExchange.exchange(serverProperties).block();
            opprettPerson(person, kilde, accessToken);
            opprettNavn(person, kilde, accessToken);
            opprettAdresse(person, kilde, accessToken);
            opprettFoedsel(person, kilde, accessToken);
            opprettTags(person, accessToken);
        } catch (Exception e) {
            throw new PdlCreatePersonException("Feil ved opprettelse person i PDL testdata", e);
        }
        return person.getIdent();
    }

    private void opprettPerson(Person person, String kilde, AccessToken token) {
        new OpprettPersonCommand(webClient, person.getIdent(), kilde, token.getTokenValue()).call();
    }

    private void opprettNavn(Person person, String kilde, AccessToken token) {
        if (person.getFornavn() != null && person.getEtternavn() != null) {
            new PostNavnCommand(webClient, person, kilde, token.getTokenValue()).call();
        }
    }

    private void opprettAdresse(Person person, String kilde, AccessToken token) {
        if (person.getAdresse() != null) {
            new PostAdresseCommand(webClient, person, kilde, token.getTokenValue()).call();
        }
    }

    private void opprettFoedsel(Person person, String kilde, AccessToken token) {
        person.toFoedselDTO(kilde)
                .ifPresent(value -> new OpprettFoedselsdatoCommand(webClient, value, token.getTokenValue(), person.getIdent()).call());
    }

    private void opprettTags(Person person, AccessToken token) {
        if (person.getTags() != null && !person.getTags().isEmpty()) {
            new PostTagsCommand(webClient, person, token.getTokenValue()).call();
        }
    }
}