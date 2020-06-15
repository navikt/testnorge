package no.nav.registre.testnorge.elsam.consumer.rs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.testnorge.elsam.consumer.rs.response.ereg.EregResponse;

@Component
public class EregConsumer {

    private RestTemplate restTemplate;
    private UriTemplate eregUrl;

    public EregConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${ereg.api.url}") String eregServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.eregUrl = new UriTemplate(eregServerUrl + "/v1/organisasjon/{orgnummer}?inkluderHierarki=false&inkluderHistorikk=false");
    }

    public EregResponse hentEregdataFraOrgnummer(String orgnummer) {
        RequestEntity<?> getRequest = RequestEntity.get(eregUrl.expand(orgnummer))
                .header("Nav-Call-Id", "orkestratoren")
                .header("Nav-Consumer-Id", "orkestratoren")
                .build();
        return restTemplate.exchange(getRequest, EregResponse.class).getBody();
    }
}
