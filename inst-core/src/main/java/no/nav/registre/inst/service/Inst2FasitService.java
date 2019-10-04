package no.nav.registre.inst.service;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.inst.fasit.FasitApiConsumer;
import no.nav.registre.inst.fasit.FasitException;
import no.nav.registre.inst.fasit.FasitResourceWithUnmappedProperties;

@Service
public class Inst2FasitService {

    public static final String FASIT_FEILMELDING = "Ugyldig miljø/miljø ikke funnet.";

    private static final String REST_SERVICE = "RestService";
    private static final String INST2_WEB_API = "inst2.web-api";

    @Autowired
    private FasitApiConsumer fasitApiConsumer;

    private static final String FAGSYSTEM = "fss";

    private Map<String, String> urlPerEnv;
    private LocalDateTime expiry;

    public String getUrlForEnv(String environment) {
        if (hasExpired()) {
            synchronized (this) {
                if (hasExpired()) {
                    FasitResourceWithUnmappedProperties[] fasitResources = fasitApiConsumer.fetchResources(INST2_WEB_API, REST_SERVICE);

                    urlPerEnv = Arrays.stream(fasitResources)
                            .filter(resource -> FAGSYSTEM.equals(resource.getScope().getZone()))
                            .collect(Collectors.toMap(
                                    resource -> resource.getScope().getEnvironment(),
                                    resource -> ((Map) resource.getProperties()).get("url") + ""));

                    expiry = LocalDateTime.now().plusHours(4);
                }
            }
        }

        if (urlPerEnv.containsKey(environment)) {
            return urlPerEnv.get(environment);
        } else {
            throw new RuntimeException(FASIT_FEILMELDING);
        }
    }

    private boolean hasExpired() {
        return (isNull(expiry) || LocalDateTime.now().isAfter(expiry));
    }

    public String getFregTokenProviderInEnvironment(String environment) {
        FasitResourceWithUnmappedProperties[] restServices = fasitApiConsumer.fetchResources("freg-token-provider-v1", "RestService");
        for (FasitResourceWithUnmappedProperties fasitResourceWithUnmappedProperties : restServices) {
            if (environment.toLowerCase().substring(0, 1).equals(fasitResourceWithUnmappedProperties.getScope().getEnvironmentclass())) {
                Map<String, String> properties = new ObjectMapper().convertValue(fasitResourceWithUnmappedProperties.getProperties(), Map.class);
                return properties.get("url");
            }
        }
        throw new FasitException("Fant ikke freg-token-provider i gitt miljø");
    }
}
