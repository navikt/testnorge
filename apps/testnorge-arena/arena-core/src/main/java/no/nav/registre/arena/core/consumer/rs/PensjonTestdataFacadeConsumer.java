package no.nav.registre.arena.core.consumer.rs;

import no.nav.registre.arena.core.consumer.rs.command.PostPensjonTestdataInntektCommand;
import no.nav.registre.arena.core.consumer.rs.command.PostPensjonTestdataPersonCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.arena.core.consumer.rs.request.PensjonTestdataInntekt;
import no.nav.registre.arena.core.consumer.rs.request.PensjonTestdataPerson;
import no.nav.registre.arena.core.consumer.rs.response.PensjonTestdataResponse;
import no.nav.registre.arena.core.security.TokenService;

@Component
public class PensjonTestdataFacadeConsumer {

    private final WebClient webClient;
    private final TokenService tokenService;

    public PensjonTestdataFacadeConsumer(
            TokenService tokenService,
            @Value("${pensjon-testdata-facade.rest-api.url}") String pensjonTestdataServerUrl
    ) {
        this.webClient = WebClient.builder().baseUrl(pensjonTestdataServerUrl).build();
        this.tokenService = tokenService;
    }

    public PensjonTestdataResponse opprettPerson(
            PensjonTestdataPerson person
    ) {
        return new PostPensjonTestdataPersonCommand(webClient, person, tokenService.getIdToken()).call();
    }

    public PensjonTestdataResponse opprettInntekt(
            PensjonTestdataInntekt inntekt
    ) {
        return new PostPensjonTestdataInntektCommand(webClient, inntekt, tokenService.getIdToken()).call();
    }
}
