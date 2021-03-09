package no.nav.registre.testnorge.opprettpersonpdl.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.common.command.CreatePersonCommand;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.opprettpersonpdl.config.credentials.PersonServiceProperties;
import no.nav.registre.testnorge.opprettpersonpdl.domain.Person;

@Slf4j
@Component
public class PersonApiConsumer {
    private final WebClient webClient;
    private final PersonServiceProperties serviceProperties;
    private final AccessTokenService accessTokenService;

    public PersonApiConsumer(
            PersonServiceProperties serviceProperties,
            AccessTokenService accessTokenService,
            ObjectMapper objectMapper
    ) {
        this.accessTokenService = accessTokenService;
        this.serviceProperties = serviceProperties;

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


    public void createPerson(Person person) {
        AccessToken accessToken = accessTokenService.generateToken(serviceProperties);
        new CreatePersonCommand(webClient, person.toDTO(), accessToken.getTokenValue(), person.toKommaseparerteTags()).run();
    }
}
