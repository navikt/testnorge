package no.nav.registre.skd.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.skd.consumer.credential.PersonServiceProperties;
import no.nav.testnav.libs.commands.CreatePersonCommand;
import no.nav.testnav.libs.dto.person.v1.PersonDTO;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;

@Component
public class PersonConsumer {
    private final WebClient webClient;
    private final AccessTokenService tokenService;
    private final ServerProperties serviceProperties;

    public PersonConsumer(
            PersonServiceProperties personServiceProperties,
            AccessTokenService accessTokenService,
            ObjectMapper objectMapper
    ) {
        this.serviceProperties = personServiceProperties;
        this.tokenService = accessTokenService;

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
                .baseUrl(personServiceProperties.getUrl())
                .build();
    }

    public void createPerson(PersonDTO person, String kilde) {
        var accessToken = tokenService.generateClientCredentialAccessToken(serviceProperties).block().getTokenValue();
        new CreatePersonCommand(webClient, person, accessToken, kilde).run();
    }
}
