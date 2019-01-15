package no.nav.registre.skd.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import no.nav.registre.skd.service.Endringskoder;
import no.nav.registre.skd.skdmelding.RsMeldingstype;

@Component
public class HodejegerenConsumer {

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public HodejegerenConsumer(@Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenServerUrl) {
        this.url = new UriTemplate(hodejegerenServerUrl + "/v1/eksisterende-identer");
    }

    public List<String> finnLevendeIdenter(Long avspillergruppeId, String miljoe) {
        RequestEntity getRequest = RequestEntity.get(url.expand(avspillergruppeId.toString(), miljoe)).build();
        return null;
    }

    public List<String> finnDoedeOgUtvandredeIdenter(Long avspillergruppeId, String miljoe) {
        RequestEntity getRequest = RequestEntity.get(url.expand(avspillergruppeId.toString(), miljoe)).build();
        return null;
    }

    public List<String> finnGifteIdenter(Long avspillergruppeId, String miljoe) {
        RequestEntity getRequest = RequestEntity.get(url.expand(avspillergruppeId.toString(), miljoe)).build();
        return null;
    }

    public Map<String, String> getStatusQuoFraAarsakskode(Endringskoder endringskode, String environment, String fnr) throws IOException {
        return null;
    }

    public List<Long> lagreSkdEndringsmeldingerITPSF(Long gruppeId, List<RsMeldingstype> skdmeldinger) {
        return null;
    }
}
