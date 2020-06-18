package no.nav.registre.testnorge.elsam.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.elsam.consumer.rs.response.tss.TssResponse;

@Slf4j
@Component
@DependencyOn("testnorge-tss")
public class TSSConsumer {

    private static final ParameterizedTypeReference<Map<String, TssResponse>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestTemplate restTemplate;
    private final UriTemplate hentLegerFraTssUrl;

    public TSSConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge.rest-api.tss}") String serverUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.hentLegerFraTssUrl = new UriTemplate(serverUrl + "/v1/ident/samhandlere/{avspillergruppeId}?miljoe={miljoe}");
    }

    public ResponseEntity<Map<String, TssResponse>> hentLeger(Long avspillergruppeId, String miljoe) {
        var getRequest = RequestEntity.get(hentLegerFraTssUrl.expand(avspillergruppeId, miljoe)).build();
        var response = restTemplate.exchange(getRequest, RESPONSE_TYPE);
        if (!response.getStatusCode().is2xxSuccessful()) {
            if (response.getStatusCode().is4xxClientError()) {
                log.error("Brukerfeil ved henting av lege i miljoe {}, på gruppe {}", miljoe, avspillergruppeId);
                throw new HttpClientErrorException(response.getStatusCode(), String.format("Brukerfeil ved henting av lege i miljoe %s, på gruppe %s", miljoe, avspillergruppeId));
            }
            if (response.getStatusCode().is5xxServerError()) {
                log.error("Feil under henting av lege. Feil skjedde i tss adapter");
                throw new HttpServerErrorException(response.getStatusCode(), "Feil under henting av lege. Feil skjedde i tss adapter");
            }
        }

        return response;
    }
}
