package no.nav.registre.testnorge.person.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.person.consumer.command.GetTagsCommand;
import no.nav.registre.testnorge.person.consumer.command.OpprettFoedselCommand;
import no.nav.registre.testnorge.person.consumer.command.OpprettPersonCommand;
import no.nav.registre.testnorge.person.consumer.command.PostAdresseCommand;
import no.nav.registre.testnorge.person.consumer.command.PostNavnCommand;
import no.nav.registre.testnorge.person.consumer.command.PostTagsCommand;
import no.nav.registre.testnorge.person.domain.Person;
import no.nav.registre.testnorge.person.exception.PdlCreatePersonException;
import no.nav.registre.testnorge.person.service.StsOidcTokenService;

@Slf4j
@Component
public class PdlTestdataConsumer {

    private final StsOidcTokenService tokenService;
    private final WebClient webClient;

    public PdlTestdataConsumer(
            StsOidcTokenService tokenService,
            ObjectMapper objectMapper,
            @Value("${system.pdl.pdlTestdataUrl}") String pdlTestdataUrl
    ) {
        this.tokenService = tokenService;
        ExchangeStrategies jacksonStrategy = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    config.defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                }).build();
        this.webClient = WebClient.builder()
                .baseUrl(pdlTestdataUrl)
                .exchangeStrategies(jacksonStrategy)
                .build();
    }

    public String createPerson(Person person, String kilde) {
        String token = tokenService.getIdToken();
        log.info("Oppretter person med ident {} i PDL", person.getIdent());

        List<Callable<? extends Object>> commands = new ArrayList<>();

        commands.add(new OpprettPersonCommand(webClient, person.getIdent(), kilde, token));

        if (person.getFornavn() != null && person.getEtternavn() != null) {
            commands.add(new PostNavnCommand(webClient, person, kilde, token));
        }

        if (person.getAdresse() != null) {
            commands.add(new PostAdresseCommand(webClient, person, kilde, token));
        }

        person.toFoedselDTO(kilde)
                .ifPresent(value -> commands.add(new OpprettFoedselCommand(webClient, value, token, person.getIdent())));

        if (person.getTags() != null && !person.getTags().isEmpty()) {
            commands.add(new PostTagsCommand(webClient, person, token));
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

    public Set<String> getTags(String ident) {
        String token = tokenService.getIdToken();
        return new GetTagsCommand(webClient, ident, token).call();
    }
}
