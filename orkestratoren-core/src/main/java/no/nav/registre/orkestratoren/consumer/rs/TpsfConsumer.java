package no.nav.registre.orkestratoren.consumer.rs;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.orkestratoren.consumer.rs.requests.SendToTpsRequest;
import no.nav.registre.orkestratoren.consumer.rs.response.AvspillingResponse;

@Component
public class TpsfConsumer {

    @Autowired
    private RestTemplate restTemplateTpsf;

    private UriTemplate uriTemplate;
    private String urlGetIdenter;

    public TpsfConsumer(@Value("${tps-forvalteren.rest-api.url}") String tpsfServerUrl) {
        uriTemplate = new UriTemplate(tpsfServerUrl + "/v1/endringsmelding/skd/send/{skdMeldingGruppeId}");
        this.urlGetIdenter = tpsfServerUrl + "/v1/endringsmelding/skd/identer/{gruppeId}?aarsakskode={aarsakskoder}&transaksjonstype={transaksjonstype}";
    }

    public AvspillingResponse sendSkdmeldingerTilTps(Long skdMeldingGruppeId, SendToTpsRequest sendToTpsRequest) {
        String url = uriTemplate.expand(skdMeldingGruppeId).toString();

        return restTemplateTpsf.postForObject(url,
                sendToTpsRequest,
                AvspillingResponse.class);
    }

    public Set<String> getIdenterFiltrertPaaAarsakskode(Long gruppeId, List<String> aarsakskoder, String transaksjonstype) {
        return restTemplateTpsf.getForObject(urlGetIdenter, LinkedHashSet.class, gruppeId, StringUtils.join(aarsakskoder, ','), transaksjonstype);
    }
}
