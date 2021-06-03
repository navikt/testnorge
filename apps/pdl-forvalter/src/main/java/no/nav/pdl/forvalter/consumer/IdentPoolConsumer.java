package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.credentials.IdentPoolProperties;
import no.nav.pdl.forvalter.consumer.command.IdentpoolPostCommand;
import no.nav.pdl.forvalter.dto.HentIdenterRequest;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
public class IdentPoolConsumer {

    private static final String ACQUIRE_IDENTS_URL = "/api/v1/identifikator";
    private static final String RELEASE_IDENTS_URL = ACQUIRE_IDENTS_URL + "/frigjoer";
    private static final String REKVIRERT_AV = "rekvirertAv=PDLF";

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;

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
            var accessToken = accessTokenService.generateToken(properties);
            var idents = new IdentpoolPostCommand(webClient, ACQUIRE_IDENTS_URL, null, request, accessToken.getTokenValue()).call();

            log.info("Identpool allokering av identer tok {} ms", currentTimeMillis() - startTime);
            return idents;

        } catch (HttpClientErrorException e) {

            log.info("Oppslag til identpool feilet etter {} ms {}", currentTimeMillis() - startTime, e.getResponseBodyAsString());
            throw new HttpClientErrorException(INTERNAL_SERVER_ERROR, format("Forspørsel %s til ident-pool feilet: %s",
                    request.toString(), e.getResponseBodyAsString()));
        }
    }

    public void releaseIdents(List<String> identer) {

        var startTime = currentTimeMillis();

        try {
            var accessToken = accessTokenService.generateToken(properties);
            var idents = new IdentpoolPostCommand(webClient, RELEASE_IDENTS_URL, REKVIRERT_AV, identer, accessToken.getTokenValue()).call();

            log.info("Identpool frigjoering av identer tok {} ms", currentTimeMillis() - startTime);

        } catch (HttpClientErrorException e) {

            log.info("Oppslag til identpool feilet etter {} ms {}", currentTimeMillis() - startTime, e.getResponseBodyAsString());
            throw new HttpClientErrorException(INTERNAL_SERVER_ERROR, format("Forspørsel %s til ident-pool feilet: %s",
                    identer.stream().collect(Collectors.joining(",")), e.getResponseBodyAsString()));
        }
    }
}
