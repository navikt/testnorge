package no.nav.testnav.apps.personservice.consumer.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class PdlTestdataConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public PdlTestdataConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            ObjectMapper objectMapper,
            WebClient webClient
    ) {
        serverProperties = consumers.getPdlProxy();
        this.tokenExchange = tokenExchange;
        var jacksonStrategy = ExchangeStrategies
                .builder()
                .codecs(config -> {
                    config
                            .defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    config
                            .defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                })
                .build();
        this.webClient = webClient
                .mutate()
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(serverProperties.getUrl())
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
                .ifPresent(value -> new OpprettFoedselsdatoCommand(webClient, value, token.getTokenValue(), person.getIdent()).call());
    }

    private void opprettTags(Person person, AccessToken token) {
        if (person.getTags() != null && !person.getTags().isEmpty()) {
            new PostTagsCommand(webClient, person, token.getTokenValue()).call();
        }
    }

    public Mono<String> ordrePerson(Person person, String kilde) {
        log.info("Oppretter person med ident {} i PDL", person.getIdent());

        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> {
                    opprettPerson(person, kilde, accessToken);
                    opprettNavn(person, kilde, accessToken);
                    opprettAdresse(person, kilde, accessToken);
                    opprettFoedsel(person, kilde, accessToken);
                    opprettTags(person, accessToken);
                    return Mono.just(person.getIdent());
                })
                .onErrorMap(Exception.class, e -> new PdlCreatePersonException("Feil ved opprettelse person i PDL testdata", e));
    }
}