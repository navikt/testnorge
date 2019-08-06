package no.nav.dolly.aareg;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.fasit.FasitApiConsumer;
import no.nav.dolly.fasit.FasitResourceWithUnmappedProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AaregArbeidsforholdFasitConsumer {

    private static final String REST_SERVICE = "RestService";
    private static final String AAREG_REST_ALIAS = "aareg.api";
    private static final String ARBEIDSFORHOLD_SERVICE_URL = "/v1/arbeidstaker/arbeidsforhold";

    private static final String FAGSYSTEM = "fss";

    private final FasitApiConsumer fasitApiConsumer;

    private Map<String, String> urlPerEnv;
    private LocalDateTime expiry;

    public String getUrlForEnv(String environment) {

        if (hasExpired()) {

            synchronized (this) {
                if (hasExpired()) {
                    FasitResourceWithUnmappedProperties[] fasitResources = fasitApiConsumer.fetchResources(AAREG_REST_ALIAS, REST_SERVICE);

                    urlPerEnv = asList(fasitResources).stream()
                            .filter(resource -> FAGSYSTEM.equals(resource.getScope().getZone()))
                            .collect(Collectors.toMap(
                                    resource -> resource.getScope().getEnvironment(),
                                    resource -> ((Map) resource.getProperties()).get("url") + ARBEIDSFORHOLD_SERVICE_URL));

                    expiry = LocalDateTime.now().plusHours(4);
                }
            }
        }

        if (urlPerEnv.containsKey(environment)) {
            return urlPerEnv.get(environment);
        } else {
            throw new DollyFunctionalException("Ugyldig miljø/miljø ikke funnet.");
        }
    }

    private boolean hasExpired() {
        return (isNull(expiry) || LocalDateTime.now().isAfter(expiry));
    }
}
