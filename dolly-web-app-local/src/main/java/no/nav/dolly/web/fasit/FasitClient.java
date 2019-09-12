package no.nav.dolly.web.fasit;

import static no.nav.freg.fasit.utils.domain.ResourceType.REST_SERVICE;
import static no.nav.freg.fasit.utils.domain.Zone.FSS;

import no.nav.freg.fasit.utils.FasitService;
import no.nav.freg.fasit.utils.domain.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Profile("fasit")
public class FasitClient {

    @Autowired
    private FasitService fasitService;

    @Value("${application.name}")
    private String applicationName;

    @Value("${test.environment}")
    private String environmentName;

    public Map<String, Object> resolveFasitProperties() {
        Map<String, Object> properties = new HashMap<>();
       //TODO
//        RestService dollyRest = fasitService.find("dolly.api.v1", REST_SERVICE, environmentName, applicationName, FSS, RestService.class);
//        properties.put("dolly.url", dollyRest.getEndpointUrl());

        RestService tpfsRest = fasitService.find("tps-forvalteren.rest-api", REST_SERVICE, environmentName, applicationName, FSS, RestService.class);
        properties.put("tpsf.url", tpfsRest.getEndpointUrl());

        return properties;
    }
}
