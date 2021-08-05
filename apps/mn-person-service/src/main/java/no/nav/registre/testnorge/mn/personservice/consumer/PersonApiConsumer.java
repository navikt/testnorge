package no.nav.registre.testnorge.mn.personservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.testnav.libs.commands.GetPersonCommand;
import no.nav.testnav.libs.dto.person.v1.PersonDTO;
import no.nav.testnav.libs.dto.person.v1.Persondatasystem;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.service.ClientCredentialGenerateAccessTokenService;
import no.nav.registre.testnorge.mn.personservice.credentials.PersonApiClientProperties;
import no.nav.registre.testnorge.mn.personservice.domain.Person;

@Slf4j
@Component
public class PersonApiConsumer {
    private final WebClient webClient;
    private final PersonApiClientProperties personApiClientProperties;
    private final ClientCredentialGenerateAccessTokenService accessTokenService;
    private final Executor executor;

    public PersonApiConsumer(
            ObjectMapper objectMapper,
            PersonApiClientProperties personApiClientProperties,
            ClientCredentialGenerateAccessTokenService accessTokenService
    ) {
        this.personApiClientProperties = personApiClientProperties;
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
                .baseUrl(personApiClientProperties.getBaseUrl())
                .build();
        this.executor = Executors.newFixedThreadPool(personApiClientProperties.getThreads());
    }

    private CompletableFuture<PersonDTO> getPerson(String ident, AccessToken accessToken) {

        return CompletableFuture.supplyAsync(
                () -> new GetPersonCommand(webClient, ident, accessToken.getTokenValue(), Persondatasystem.PDL, "q2").call(),
                executor
        );
    }

    public List<Person> getPersoner(Set<String> identer) {
        AccessToken accessToken = accessTokenService.generateToken(personApiClientProperties);
        var futures = identer.stream().map(ident -> getPerson(ident, accessToken)).collect(Collectors.toList());
        List<PersonDTO> personer = new ArrayList<>();
        for (var future : futures) {
            try {
                personer.add(future.get());
            } catch (Exception e) {
                throw new RuntimeException("Klarte ikke å hente ut person", e);
            }
        }
        return personer.stream().map(Person::new).collect(Collectors.toList());
    }
}