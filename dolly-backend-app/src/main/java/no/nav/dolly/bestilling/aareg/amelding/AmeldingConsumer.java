package no.nav.dolly.bestilling.aareg.amelding;

import no.nav.dolly.config.credentials.AmeldingServiceProperties;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.AMeldingDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

@Service
public class AmeldingConsumer {

    private final TokenService tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serverProperties;

    public AmeldingConsumer(TokenService tokenService, AmeldingServiceProperties serviceProperties) {
        this.tokenService = tokenService;
        this.serverProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    public Map<String, ResponseEntity<Void>> putAmeldingList(Map<String, AMeldingDTO> ameldingList, String miljoe) {

        AccessToken accessToken = tokenService.generateToken(serverProperties).block();

        if (nonNull(accessToken)) {
            return ameldingList.values().stream().map(amelding -> putAmeldingdata(amelding, miljoe,
                    amelding.getKalendermaaned().toString(), accessToken.getTokenValue())).findFirst().orElse(null);
        } else
            throw new DollyFunctionalException(String.format("Klarte ikke å hente accessToken for %s", serverProperties.getName()));
    }

    @Timed(name = "providers", tags = { "operation", "amelding_put" })
    public Map<String, ResponseEntity<Void>> putAmeldingdata(AMeldingDTO amelding, String miljoe,
                                                             String maaned, String accessTokenValue) {
        ResponseEntity<Void> response = webClient.put()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/amelding").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenValue)
                .header("Nav-Call-Id", generateCallId())
                .header("miljo", miljoe)
                .body(BodyInserters.fromPublisher(Mono.just(amelding), AMeldingDTO.class))
                .retrieve()
                .toBodilessEntity().block();

        if (nonNull(response)) {
            return Map.of(maaned, response);
        } else
            throw new DollyFunctionalException("Feil under innsending til Amelding-service");
    }
}
