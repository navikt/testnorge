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
import no.nav.testnav.libs.commands.CreatePersonCommand;
import no.nav.testnav.libs.dto.person.v1.PersonDTO;
import no.nav.testnav.libs.servletsecurity.domain.AccessScopes;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.domain.ClientCredential;
import no.nav.testnav.libs.servletsecurity.service.ClientCredentialGenerateAccessTokenService;


@Component
public class PersonConsumer {
    private final WebClient webClient;
    private final ClientCredential clientCredential;
    private final ClientCredentialGenerateAccessTokenService accessTokenService;

    public PersonConsumer(
            @Value("${person.rest.api.url}") String url,
            ObjectMapper objectMapper,
            PersonApiClientCredential clientCredential,
            ClientCredentialGenerateAccessTokenService accessTokenService
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

    public void createPerson(PersonDTO person, String kilde) {
        AccessToken accessToken = accessTokenService.generateToken(
                clientCredential,
                new AccessScopes("api://" + clientCredential.getClientId() + "/.default")
        );
        new CreatePersonCommand(webClient, person, accessToken.getTokenValue(), kilde).run();
    }
}
