package no.nav.registre.testnorge.person.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.person.consumer.command.GetPersonCommand;
import no.nav.registre.testnorge.person.consumer.dto.graphql.Feilmelding;
import no.nav.registre.testnorge.person.consumer.dto.graphql.PdlPerson;
import no.nav.registre.testnorge.person.domain.Person;
import no.nav.registre.testnorge.person.service.StsOidcTokenService;

@Slf4j
@Component
@DependencyOn(value = "pdl-testdata", external = true)
public class PdlApiConsumer {

    private final StsOidcTokenService tokenService;
    private final String pdlApiUrl;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    public PdlApiConsumer(
            StsOidcTokenService tokenService,
            @Value("${system.pdl.pdlApi}") String pdlApiUrl,
            RestTemplateBuilder restTemplateBuilder,
            ObjectMapper mapper
    ) {
        this.tokenService = tokenService;
        this.pdlApiUrl = pdlApiUrl;
        this.restTemplate = restTemplateBuilder.build();
        this.mapper = mapper;
    }

    public Person getPerson(String ident) throws RuntimeException {
        String token = tokenService.getIdToken();

        PdlPerson pdlPerson = new GetPersonCommand(restTemplate, pdlApiUrl, ident, token, mapper).call();

        if (pdlPerson.getErrors().isEmpty()) {
            return new Person(pdlPerson);
        }

        if (pdlPerson.getErrors()
                .stream()
                .anyMatch(value -> value.getMessage().equals("Fant ikke person"))) {
            return null;
        }

        log.error("Finner ikke person i pdl: {}", pdlPerson.getErrors()
                .stream()
                .map(Feilmelding::getMessage)
                .collect(Collectors.joining(", ")));
        throw new RuntimeException("Feil ved henting av pdl-personer");
    }
}
