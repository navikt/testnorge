package no.nav.registre.skd.consumer;

import com.google.common.collect.Lists;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.skd.consumer.requests.SendToTpsRequest;
import no.nav.registre.skd.consumer.requests.SlettSkdmeldingerRequest;
import no.nav.registre.skd.consumer.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;

@Component
@Slf4j
@DependencyOn(value = "tps-forvalteren", external = true)
public class TpsfConsumer {

    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private static final ParameterizedTypeReference<SkdMeldingerTilTpsRespons> RESPONSE_TYPE_TPS = new ParameterizedTypeReference<>() {
    };

    private static final int PAGE_SIZE = 100;

    private final RestTemplate restTemplate;
    private final UriTemplate uriTemplateSaveToTpsf;
    private final UriTemplate uriTemplateSaveToTps;
    private final UriTemplate uriTemplateGetMeldingIder;
    private final UriTemplate urlGetMeldingIder;
    private final UriTemplate uriSlettMeldinger;
    private final UriTemplate uriSlettIdenterFraTps;
    private final UriTemplate uriGetMeldingerMedIds;
    private final UriTemplate uriOppdaterSkdmelding;

    public TpsfConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${tps-forvalteren.rest-api.url}") String serverUrl,
            @Value("${testnorges.ida.credential.tpsf.username}") String username,
            @Value("${testnorges.ida.credential.tpsf.password}") String password
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
        this.uriTemplateSaveToTpsf = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/save/{gruppeId}");
        this.uriTemplateSaveToTps = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/send/{gruppeId}");
        this.uriTemplateGetMeldingIder = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/meldinger/{gruppeId}");
        this.urlGetMeldingIder = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/meldinger/{avspillergruppeId}");
        this.uriSlettMeldinger = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/deletemeldinger");
        this.uriSlettIdenterFraTps = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/deleteFromTps?miljoer={miljoer}&identer={identer}");
        this.uriGetMeldingerMedIds = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/meldinger?ids={ids}");
        this.uriOppdaterSkdmelding = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/updatemeldinger");
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public List<Long> saveSkdEndringsmeldingerInTPSF(
            Long gruppeId,
            List<RsMeldingstype> skdmeldinger
    ) {
        if (log.isInfoEnabled()) {
            log.info("Lagrer {} skd endringsmeldinger i tps-forvalteren med gruppe id {}", skdmeldinger.size(), gruppeId);
        }
        var postRequest = RequestEntity.post(uriTemplateSaveToTpsf.expand(gruppeId)).body(skdmeldinger);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public SkdMeldingerTilTpsRespons sendSkdmeldingerToTps(
            Long gruppeId,
            SendToTpsRequest sendToTpsRequest
    ) {
        var postRequest = RequestEntity.post(uriTemplateSaveToTps.expand(gruppeId)).body(sendToTpsRequest);
        log.info("Sender skd-meldinger med avspillergruppe {} til tps", gruppeId);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE_TPS).getBody();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public List<Long> getMeldingIdsFromAvspillergruppe(Long gruppeId) {
        var getRequest = RequestEntity.get(uriTemplateGetMeldingIder.expand(gruppeId)).build();
        return restTemplate.exchange(getRequest, RESPONSE_TYPE).getBody();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public List<Long> getMeldingIderTilhoerendeIdenter(
            Long avspillergruppeId,
            List<String> identer
    ) {
        var postRequest = RequestEntity.post(urlGetMeldingIder.expand(avspillergruppeId)).body(identer);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public ResponseEntity<String> slettMeldingerFraTpsf(List<Long> meldingIder) {
        var postRequest = RequestEntity.post(uriSlettMeldinger.expand()).body(SlettSkdmeldingerRequest.builder().ids(meldingIder).build());
        return restTemplate.exchange(postRequest, String.class);
    }

    @Timed
    public ResponseEntity<Object> slettIdenterFraTps(
            List<String> miljoer,
            List<String> identer
    ) {
        var response = ResponseEntity.ok().build();
        var miljoerSomString = String.join(",", miljoer);

        List<List<String>> identerPartisjonert = Lists.partition(identer, PAGE_SIZE);
        for (var partisjon : identerPartisjonert) {
            var identerSomString = String.join(",", partisjon);
            var deleteRequest = RequestEntity.delete(uriSlettIdenterFraTps.expand(miljoerSomString, identerSomString)).build();
            try {
                restTemplate.exchange(deleteRequest, ResponseEntity.class);
            } catch (HttpClientErrorException e) {
                log.error("Kunne ikke slette ident fra TPS. ", e);
            }
        }
        return response;
    }

    public List<RsMeldingstype> getMeldingerMedIds(List<String> ids) {
        List<RsMeldingstype> response = new ArrayList<>();
        for (var partisjonerteIder : Lists.partition(ids, 80)) {
            var getRequest = RequestEntity.get(uriGetMeldingerMedIds.expand(String.join(",", partisjonerteIder))).build();
            try {
                var body = restTemplate.exchange(getRequest, new ParameterizedTypeReference<List<RsMeldingstype>>() {
                }).getBody();
                if (body != null) {
                    response.addAll(body);
                }
            } catch (HttpStatusCodeException e) {
                log.error("Kunne ikke hente meldinger med ider {}", partisjonerteIder, e);
            }
        }
        return response;
    }

    public List<Long> oppdaterSkdMeldinger(List<RsMeldingstype> meldinger) {
        if (!meldinger.isEmpty()) {
            var postRequest = RequestEntity.post(uriOppdaterSkdmelding.expand()).body(meldinger);
            var response = restTemplate.exchange(postRequest, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                List<Long> meldingIdsSomIkkeKunneOppdateres = meldinger.stream().map(RsMeldingstype::getId).collect(Collectors.toList());
                log.error("Kunne ikke oppdatere meldinger med id-er: {}", meldingIdsSomIkkeKunneOppdateres);
                return meldingIdsSomIkkeKunneOppdateres;
            }
            log.info("Oppdaterte meldinger med id-er: {}", meldinger.stream().map(RsMeldingstype::getId).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }
}
