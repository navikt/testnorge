package no.nav.registre.aareg.security.sts;

import static java.util.Objects.isNull;
import static no.nav.registre.aareg.properties.Environment.PREPROD;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.aareg.exception.TestnorgeAaregFunctionalException;
import no.nav.registre.aareg.fasit.FasitApiConsumer;
import no.nav.registre.aareg.properties.Environment;

@Component
@RequiredArgsConstructor
public class StsOidcFasitConsumer {

    private static final String STS_NOT_FOUND = "Ugyldig sts-miljø/sts-miljø ikke funnet.";

    private static final String REST_SERVICE = "RestService";
    private static final String OIDC_ALIAS = "security-token-service-token";

    private static final String FAGSYSTEM = "fss";

    private final FasitApiConsumer fasitApiConsumer;

    private Map<String, String> urlOidcPerEnv;
    private LocalDateTime expiry;

    public String getStsOidcService(Environment environment) {
        if (hasExpired()) {
            synchronized (this) {
                if (hasExpired()) {
                    var fasitResources = fasitApiConsumer.fetchResources(OIDC_ALIAS, REST_SERVICE);

                    urlOidcPerEnv = Arrays.stream(fasitResources)
                            .filter(resource -> OIDC_ALIAS.equals(resource.getAlias()) &&
                                    FAGSYSTEM.equals(resource.getScope().getZone()) &&
                                    isNull(resource.getScope().getApplication()) &&
                                    isNull(resource.getScope().getEnvironment()))
                            .collect(Collectors.toMap(
                                    resource -> resource.getScope().getEnvironmentclass(),
                                    resource -> (String) ((Map) resource.getProperties()).get("url")));

                    expiry = LocalDateTime.now().plusHours(4);
                }
            }
        }

        if (urlOidcPerEnv.containsKey(environment == PREPROD ? "q" : "t")) {
            return urlOidcPerEnv.get(environment == PREPROD ? "q" : "t");
        } else {
            throw new TestnorgeAaregFunctionalException(STS_NOT_FOUND);
        }
    }

    private boolean hasExpired() {
        return (isNull(expiry) || LocalDateTime.now().isAfter(expiry));
    }
}
