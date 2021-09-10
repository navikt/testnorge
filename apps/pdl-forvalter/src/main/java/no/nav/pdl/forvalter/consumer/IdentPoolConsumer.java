package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.credentials.IdentPoolProperties;
import no.nav.pdl.forvalter.consumer.command.IdentpoolPostCommand;
import no.nav.pdl.forvalter.dto.HentIdenterRequest;
import no.nav.pdl.forvalter.exception.InternalServerException;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;

@Slf4j
@Service
public class IdentPoolConsumer {

    private static final String ACQUIRE_IDENTS_URL = "/api/v1/identifikator";
    private static final String RELEASE_IDENTS_URL = ACQUIRE_IDENTS_URL + "/frigjoer";
    private static final String REKVIRERT_AV = "rekvirertAv=PDLF";

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final ServerProperties properties;

    public IdentPoolConsumer(AccessTokenService accessTokenService, IdentPoolProperties properties) {
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public String[] getIdents(HentIdenterRequest request) {

        var startTime = currentTimeMillis();

        try {
            var idents = accessTokenService.generateToken(properties).flatMap(
                    token -> new IdentpoolPostCommand(webClient, ACQUIRE_IDENTS_URL, null, request,
                            token.getTokenValue()).call())
                    .block();

            log.info("Identpool allokering av identer tok {} ms", currentTimeMillis() - startTime);
            return idents;

        } catch (WebClientResponseException e) {

            log.info("Oppslag til identpool feilet etter {} ms {}", currentTimeMillis() - startTime, e.getResponseBodyAsString());
            throw new InternalServerException(format("Forspørsel %s til ident-pool feilet: %s",
                    request.toString(), e.getResponseBodyAsString()));
        }
    }

    public Flux<String[]> releaseIdents(List<String> identer) {

        return  Flux.from(accessTokenService.generateToken(properties).flatMap(
                    token -> new IdentpoolPostCommand(webClient, RELEASE_IDENTS_URL, REKVIRERT_AV, identer,
                            token.getTokenValue()).call()));
    }
}
