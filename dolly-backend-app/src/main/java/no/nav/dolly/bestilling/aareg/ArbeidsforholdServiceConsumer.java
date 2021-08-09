package no.nav.dolly.bestilling.aareg;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.credentials.ArbeidsforholdServiceProperties;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;

@Slf4j
@Component
public class ArbeidsforholdServiceConsumer {

    private static final String ARBEIDSTAKER_URL = "/api/v2/arbeidstaker";
    private static final String HENT_ARBEIDSFORHOLD = "%s" + ARBEIDSTAKER_URL + "/%s/arbeidsforhold";

    private final TokenService tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serverProperties;

    public ArbeidsforholdServiceConsumer(TokenService tokenService, ArbeidsforholdServiceProperties serviceProperties) {
        this.tokenService = tokenService;
        this.serverProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "aareg_getArbeidforhold" })
    public List<ArbeidsforholdResponse> hentArbeidsforhold(String ident, String miljoe) {

        try {
            String tokenValue = tokenService.generateToken(serverProperties).block().getTokenValue();

            ResponseEntity<List<ArbeidsforholdResponse>> response = webClient.get()
                    .uri(URI.create(format(HENT_ARBEIDSFORHOLD, serverProperties.getUrl(), ident)))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                    .header("miljo", miljoe)
                    .header("Nav-Call-Id", getNavCallId())
                    .retrieve()
                    .toEntityList(ArbeidsforholdResponse.class).block();

            return response.hasBody() ? response.getBody() : emptyList();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return emptyList();
            } else {
                throw e;
            }
        }
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}