package no.nav.registre.aareg.consumer.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import no.nav.registre.aareg.consumer.rs.response.MiljoerResponse;
import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;

@Component
@RequiredArgsConstructor
@DependencyOn(value = "tps-forvalteren", external = true)
public class TpsfConsumer {

    private static final String TPSF_GET_ENVIRONMENTS = "/v1/environments";

    private final RestTemplate restTemplate;

    @Value("${tps-forvalteren.rest-api.url}")
    private String tpsfServerUrl;

    public MiljoerResponse hentMiljoer() {
        var getRequest = RequestEntity.get(URI.create(tpsfServerUrl + TPSF_GET_ENVIRONMENTS)).build();
        return restTemplate.exchange(getRequest, MiljoerResponse.class).getBody();
    }
}
