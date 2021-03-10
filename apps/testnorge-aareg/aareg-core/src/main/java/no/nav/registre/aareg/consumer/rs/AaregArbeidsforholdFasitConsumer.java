package no.nav.registre.aareg.consumer.rs;

import static java.lang.String.format;
import static java.util.Objects.isNull;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.aareg.exception.TestnorgeAaregFunctionalException;
import no.nav.registre.aareg.fasit.FasitApiConsumer;

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
                    var fasitResources = fasitApiConsumer.fetchResources(AAREG_REST_ALIAS, REST_SERVICE);

                    urlPerEnv = Arrays.stream(fasitResources)
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
            throw new TestnorgeAaregFunctionalException(format("Ugyldig miljø/miljø ikke funnet, %s", environment));
        }
    }

    private boolean hasExpired() {
        return (isNull(expiry) || LocalDateTime.now().isAfter(expiry));
    }
}
