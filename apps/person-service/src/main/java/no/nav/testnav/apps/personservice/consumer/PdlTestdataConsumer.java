package no.nav.testnav.apps.personservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.personservice.consumer.command.OpprettFoedselCommand;
import no.nav.testnav.apps.personservice.consumer.command.OpprettPersonCommand;
import no.nav.testnav.apps.personservice.consumer.command.PostAdresseCommand;
import no.nav.testnav.apps.personservice.consumer.command.PostNavnCommand;
import no.nav.testnav.apps.personservice.consumer.command.PostTagsCommand;
import no.nav.testnav.apps.personservice.consumer.exception.PdlCreatePersonException;
import no.nav.testnav.apps.personservice.credentials.PdlServiceProperties;
import no.nav.testnav.apps.personservice.domain.Person;
import no.nav.testnav.libs.reactivesecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class PdlTestdataConsumer {

    private final WebClient webClient;
    private final PdlServiceProperties serviceProperties;
    private final TokenExchange tokenExchange;

    public PdlTestdataConsumer(
            PdlServiceProperties serviceProperties,
            TokenExchange tokenExchange,
            ObjectMapper objectMapper
    ) {
        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
        ExchangeStrategies jacksonStrategy = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    config.defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                }).build();

        this.webClient = WebClient
                .builder()
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(serviceProperties.getUrl())
                .build();
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
                .ifPresent(value -> new OpprettFoedselCommand(webClient, value, token.getTokenValue(), person.getIdent()).call());
    }

    private void opprettTags(Person person, AccessToken token) {
        if (person.getTags() != null && !person.getTags().isEmpty()) {
            new PostTagsCommand(webClient, person, token.getTokenValue()).call();
        }
    }

    public String createPerson(Person person, String kilde) {
        log.info("Oppretter person med ident {} i PDL", person.getIdent());

        try {
            var accessToken = tokenExchange.generateToken(serviceProperties).block();
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
}