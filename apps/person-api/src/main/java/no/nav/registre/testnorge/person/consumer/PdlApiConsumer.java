package no.nav.registre.testnorge.person.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.person.consumer.command.GetPersonCommand;
import no.nav.registre.testnorge.person.domain.Person;
import no.nav.registre.testnorge.person.service.StsOidcTokenService;

@Slf4j
@Component
@DependencyOn(value = "pdl-testdata", external = true)
public class PdlApiConsumer {

    private final StsOidcTokenService tokenService;
    private final String personforvalterUrl;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    public PdlApiConsumer(
            StsOidcTokenService tokenService,
            @Value("${system.pdl.personforvalterUrl}") String personforvalterUrl,
            RestTemplateBuilder restTemplateBuilder,
            ObjectMapper mapper
    ) {
        this.tokenService = tokenService;
        this.personforvalterUrl = personforvalterUrl;
        this.restTemplate = restTemplateBuilder.build();
        this.mapper = mapper;
    }

    public Person getPerson(String ident) {
        String token = tokenService.getIdToken();

        return new Person(new GetPersonCommand(restTemplate, personforvalterUrl, ident, token, mapper).call());
    }
}
