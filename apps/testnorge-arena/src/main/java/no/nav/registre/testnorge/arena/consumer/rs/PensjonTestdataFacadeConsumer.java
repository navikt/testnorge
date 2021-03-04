package no.nav.registre.testnorge.arena.consumer.rs;

import no.nav.registre.testnorge.arena.consumer.rs.command.pensjon.PostPensjonTestdataInntektCommand;
import no.nav.registre.testnorge.arena.consumer.rs.command.pensjon.PostPensjonTestdataPersonCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.arena.consumer.rs.request.pensjon.PensjonTestdataInntekt;
import no.nav.registre.testnorge.arena.consumer.rs.request.pensjon.PensjonTestdataPerson;
import no.nav.registre.testnorge.arena.consumer.rs.response.pensjon.PensjonTestdataResponse;
import no.nav.registre.testnorge.arena.security.TokenService;

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
