package no.nav.registre.skd.consumer;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import no.nav.registre.skd.consumer.requests.SendToTpsRequest;
import no.nav.registre.skd.consumer.requests.SlettSkdmeldingerRequest;
import no.nav.registre.skd.consumer.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.skd.skdmelding.RsMeldingstype;

@Component
@Slf4j
public class TpsfConsumer {

    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private static final ParameterizedTypeReference<SkdMeldingerTilTpsRespons> RESPONSE_TYPE_TPS = new ParameterizedTypeReference<>() {
    };

    private RestTemplate restTemplate;
    private UriTemplate uriTemplateSaveToTpsf;
    private UriTemplate uriTemplateSaveToTps;
    private UriTemplate uriTemplateGetMeldingIder;
    private UriTemplate urlGetMeldingIder;
    private UriTemplate urlSlettMeldinger;
    private UriTemplate urlSlettIdenterFraTps;

    public TpsfConsumer(RestTemplateBuilder restTemplateBuilder,
            @Value("${tps-forvalteren.rest-api.url}") String serverUrl,
            @Value("${testnorges.ida.credential.tpsf.username}") String username,
            @Value("${testnorges.ida.credential.tpsf.password}") String password
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
        this.uriTemplateSaveToTpsf = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/save/{gruppeId}");
        this.uriTemplateSaveToTps = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/send/{gruppeId}");
        this.uriTemplateGetMeldingIder = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/meldinger/{gruppeId}");
        this.urlGetMeldingIder = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/meldinger/{avspillergruppeId}");
        this.urlSlettMeldinger = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/deletemeldinger");
        this.urlSlettIdenterFraTps = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/deleteFromTps?miljoer={miljoer}&identer={identer}");
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public List<Long> saveSkdEndringsmeldingerInTPSF(Long gruppeId, List<RsMeldingstype> skdmeldinger) {
        var postRequest = RequestEntity.post(uriTemplateSaveToTpsf.expand(gruppeId)).body(skdmeldinger);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public SkdMeldingerTilTpsRespons sendSkdmeldingerToTps(Long gruppeId, SendToTpsRequest sendToTpsRequest) {
        var postRequest = RequestEntity.post(uriTemplateSaveToTps.expand(gruppeId)).body(sendToTpsRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE_TPS).getBody();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public List<Long> getMeldingIdsFromAvspillergruppe(Long gruppeId) {
        var getRequest = RequestEntity.get(uriTemplateGetMeldingIder.expand(gruppeId)).build();
        return restTemplate.exchange(getRequest, RESPONSE_TYPE).getBody();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public List<Long> getMeldingIderTilhoerendeIdenter(Long avspillergruppeId, List<String> identer) {
        var postRequest = RequestEntity.post(urlGetMeldingIder.expand(avspillergruppeId)).body(identer);
        return new ArrayList<>(Objects.requireNonNull(restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody()));
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public ResponseEntity slettMeldingerFraTpsf(List<Long> meldingIder) {
        var postRequest = RequestEntity.post(urlSlettMeldinger.expand()).body(SlettSkdmeldingerRequest.builder().ids(meldingIder).build());
        return restTemplate.exchange(postRequest, ResponseEntity.class);
    }

    @Timed
    public ResponseEntity slettIdenterFraTps(List<String> miljoer, List<String> identer) {
        var miljoerSomString = String.join(",", miljoer);
        var identerSomString = String.join(",", identer);
        var deleteRequest = RequestEntity.delete(urlSlettIdenterFraTps.expand(miljoerSomString, identerSomString)).build();
        ResponseEntity response = null;
        try {
            response = restTemplate.exchange(deleteRequest, ResponseEntity.class);
        } catch (HttpClientErrorException e) {
            log.error("Kunne ikke slette ident fra TPS. ", e);
        }
        return response;
    }
}
