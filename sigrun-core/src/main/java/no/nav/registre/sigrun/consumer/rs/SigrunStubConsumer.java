package no.nav.registre.sigrun.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.sigrun.PoppSyntetisererenResponse;
import no.nav.registre.sigrun.consumer.rs.responses.SigrunSkattegrunnlagResponse;

@Component
@Slf4j
public class SigrunStubConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };
    private static final ParameterizedTypeReference<List<SigrunSkattegrunnlagResponse>> RESPONSE_TYPE_HENT_SKATTEGRUNNLAG = new ParameterizedTypeReference<List<SigrunSkattegrunnlagResponse>>() {
    };
    private static final String NAV_CALL_ID = "orkestratoren";
    private static final String NAV_CONSUMER_ID = "orkestratoren";

    @Autowired
    private RestTemplate restTemplate;

    private String sigrunBaseUrl;

    public SigrunStubConsumer(@Value("${sigrunstub.url}") String sigrunServerUrl) {
        this.sigrunBaseUrl = sigrunServerUrl;
    }

    @Timed(value = "testnorge-sigrun.resource.latency", extraTags = { "operation", "sigrun-skd-stub" })
    public List<String> hentEksisterendePersonidentifikatorer(String miljoe, String testdataEier) {
        UriTemplate hentFnrUrl;
        RequestEntity getRequest;
        if (testdataEier != null) {
            hentFnrUrl = new UriTemplate(String.format(sigrunBaseUrl, miljoe) + "testdata/hentPersonidentifikatorer?testdataEier={testdataEier}");
            getRequest = RequestEntity.get(hentFnrUrl.expand(testdataEier)).build();
        } else {
            hentFnrUrl = new UriTemplate(String.format(sigrunBaseUrl, miljoe) + "testdata/hentPersonidentifikatorer");
            getRequest = RequestEntity.get(hentFnrUrl.expand()).build();
        }
        ResponseEntity<List<String>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE);

        List<String> identer = new ArrayList<>();

        if (response.getBody() != null) {
            identer.addAll(response.getBody());
        } else {
            log.error("SigrunStubConsumer.hentEksisterendePersonidentifikatorer: Kunne ikke hente response body fra sigrun-skd-stub: NullPointerException");
        }

        return identer;
    }

    @Timed(value = "testnorge-sigrun.resource.latency", extraTags = { "operation", "sigrun-skd-stub" })
    public ResponseEntity sendDataTilSigrunstub(List<PoppSyntetisererenResponse> meldinger, String testdataEier, String miljoe) {
        UriTemplate sendDataUrl = new UriTemplate(String.format(sigrunBaseUrl, miljoe) + "testdata/opprettBolk");
        RequestEntity postRequest = RequestEntity.post(sendDataUrl.expand()).header("testdataEier", testdataEier).body(meldinger);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }

    @Timed(value = "testnorge-sigrun.resource.latency", extraTags = { "operation", "sigrun-skd-stub" })
    public List<SigrunSkattegrunnlagResponse> hentEksisterendeSkattegrunnlag(String ident, String miljoe) {
        UriTemplate hentSkattegrunnlagUrl = new UriTemplate(String.format(sigrunBaseUrl, miljoe) + "testdata/les");
        RequestEntity getRequest = RequestEntity.get(hentSkattegrunnlagUrl.expand())
                .header("Nav-Call-Id", NAV_CALL_ID)
                .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                .header("personidentifikator", ident)
                .build();
        return restTemplate.exchange(getRequest, RESPONSE_TYPE_HENT_SKATTEGRUNNLAG).getBody();
    }

    @Timed(value = "testnorge-sigrun.resource.latency", extraTags = { "operation", "sigrun-skd-stub" })
    public ResponseEntity slettEksisterendeSkattegrunnlag(SigrunSkattegrunnlagResponse skattegrunnlag, String miljoe) {
        UriTemplate slettSkattegrunnlagUrl = new UriTemplate(String.format(sigrunBaseUrl, miljoe) + "testdata/slett");
        RequestEntity deleteRequest = RequestEntity.delete(slettSkattegrunnlagUrl.expand())
                .header("Nav-Call-Id", NAV_CALL_ID)
                .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                .header("personidentifikator", skattegrunnlag.getPersonidentifikator())
                .header("grunnlag", skattegrunnlag.getGrunnlag())
                .header("inntektsaar", skattegrunnlag.getInntektsaar())
                .header("tjeneste", skattegrunnlag.getTjeneste())
                .build();
        return restTemplate.exchange(deleteRequest, ResponseEntity.class);
    }
}
