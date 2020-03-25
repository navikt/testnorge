package no.nav.registre.arena.core.pensjon.consumer.rs;

import static no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils.AUTHORIZATION_HEADER;
import static no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils.CALL_ID_HEADER;
import static no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils.CONSUMER_ID_HEADER;
import static no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils.NAV_CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils.NAV_CONSUMER_ID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.arena.core.pensjon.request.PensjonTestdataInntekt;
import no.nav.registre.arena.core.pensjon.request.PensjonTestdataPerson;
import no.nav.registre.arena.core.pensjon.response.PensjonTestdataResponse;
import no.nav.registre.arena.core.security.TokenService;

@Component
public class PensjonTestdataFacadeConsumer {

    private final RestTemplate restTemplate;

    @Autowired
    private TokenService tokenService;

    private UriTemplate opprettPersonUrl;
    private UriTemplate opprettInntektUrl;

    public PensjonTestdataFacadeConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${pensjon-testdata-facade.rest-api.url}") String pensjonTestdataServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.opprettPersonUrl = new UriTemplate(pensjonTestdataServerUrl + "/v1/person");
        this.opprettInntektUrl = new UriTemplate(pensjonTestdataServerUrl + "/v1/inntekt");
    }

    public PensjonTestdataResponse opprettPerson(
            PensjonTestdataPerson person
    ) {
        var postRequest = RequestEntity.post(opprettPersonUrl.expand())
                .header(CALL_ID_HEADER, NAV_CALL_ID)
                .header(CONSUMER_ID_HEADER, NAV_CONSUMER_ID)
                .header(AUTHORIZATION_HEADER, tokenService.getIdToken())
                .body(person);
        return restTemplate.exchange(postRequest, PensjonTestdataResponse.class).getBody();
    }

    public PensjonTestdataResponse opprettInntekt(
            PensjonTestdataInntekt inntekt
    ) {
        var postRequest = RequestEntity.post(opprettInntektUrl.expand())
                .header(CALL_ID_HEADER, NAV_CALL_ID)
                .header(CONSUMER_ID_HEADER, NAV_CONSUMER_ID)
                .header(AUTHORIZATION_HEADER, tokenService.getIdToken())
                .body(inntekt);
        return restTemplate.exchange(postRequest, PensjonTestdataResponse.class).getBody();
    }
}
