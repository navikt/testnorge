package no.nav.registre.orkestratoren.consumer.rs;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TpsfConsumer {

    @Autowired
    private RestTemplate restTemplateTpsf;

    private String urlGetIdenter;

    public TpsfConsumer(@Value("${tps-forvalteren.rest-api.url}") String tpsfServerUrl) {
        this.urlGetIdenter = tpsfServerUrl + "/v1/endringsmelding/skd/identer/{avspillergruppeId}?aarsakskode={aarsakskoder}&transaksjonstype={transaksjonstype}";
    }

    public Set<String> getIdenterFiltrertPaaAarsakskode(Long avspillergruppeId, List<String> aarsakskoder, String transaksjonstype) {
        return restTemplateTpsf.getForObject(urlGetIdenter, LinkedHashSet.class, avspillergruppeId, StringUtils.join(aarsakskoder, ','), transaksjonstype);
    }
}
