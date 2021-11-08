package no.nav.dolly.bestilling.aareg;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.credentials.ArbeidsforholdServiceProperties;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@Component
public class ArbeidsforholdServiceConsumer {

    private static final String ARBEIDSTAKER_URL = "/api/v2/arbeidstaker";
    private static final String HENT_ARBEIDSFORHOLD = "%s" + ARBEIDSTAKER_URL + "/%s/arbeidsforhold";

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public ArbeidsforholdServiceConsumer(TokenExchange tokenService, ArbeidsforholdServiceProperties serviceProperties) {
        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "aareg_getArbeidforhold" })
    public List<ArbeidsforholdResponse> hentArbeidsforhold(String ident, String miljoe) {

        try {
            String tokenValue = serviceProperties.getAccessToken(tokenService);

            ResponseEntity<List<ArbeidsforholdResponse>> response = webClient.get()
                    .uri(URI.create(format(HENT_ARBEIDSFORHOLD, serviceProperties.getUrl(), ident)))
                    .header(HttpHeaders.AUTHORIZATION, tokenValue)
                    .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                    .header("miljo", miljoe)
                    .header("Nav-Call-Id", getNavCallId())
                    .retrieve()
                    .toEntityList(ArbeidsforholdResponse.class).block();

            return response.hasBody() ? response.getBody() : emptyList();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return emptyList();
            } else {
                throw e;
            }
        }
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}