package no.nav.registre.skd.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.skd.consumer.credential.PersonApiClientCredential;
import no.nav.registre.testnorge.common.command.CreatePersonCommand;
import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.dto.person.v1.PersonDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;


@Component
@DependencyOn("person-api")
public class PersonConsumer {
    private final WebClient webClient;
    private final ClientCredential clientCredential;
    private final AccessTokenService accessTokenService;

    public PersonConsumer(
            @Value("${person.rest.api.url}") String url,
            ObjectMapper objectMapper,
            PersonApiClientCredential clientCredential,
            AccessTokenService accessTokenService
    ) {
        this.clientCredential = clientCredential;
        this.accessTokenService = accessTokenService;

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
                .baseUrl(url)
                .build();
    }

    public void createPerson(PersonDTO person) {
        AccessToken accessToken = accessTokenService.generateToken(clientCredential);
        new CreatePersonCommand(webClient, person, accessToken.getTokenValue()).run();
    }
}
