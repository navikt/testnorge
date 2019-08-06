package no.nav.dolly.sts;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.properties.Environment.PREPROD;
import static no.nav.dolly.properties.Environment.TEST;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.fasit.FasitApiConsumer;
import no.nav.dolly.fasit.FasitResourceWithUnmappedProperties;
import no.nav.dolly.properties.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StsSamlFasitConsumer {

    private static final String STS_NOT_FOUND = "Ugyldig sts-miljø/sts-miljø ikke funnet.";
    private static final String BASE_URL = "BaseUrl";
    private static final String SAML_ALIAS = "securityTokenService";
    private static final String FAGSYSTEM = "fss";

    private final FasitApiConsumer fasitApiConsumer;

    private Map<String, String> urlSamlPerEnv;
    private LocalDateTime expiry;

    public String getStsSamlService(Environment environment) {

        if (hasExpired()) {
            updateInventory();
        }

        for (Map.Entry<String, String> entry : urlSamlPerEnv.entrySet()) {
            if ((environment == PREPROD && (entry.getKey().contains("q"))) ||
                    (environment == TEST && (entry.getKey().contains("t")))) {
                return entry.getValue();
            }
        }

        throw new DollyFunctionalException(STS_NOT_FOUND);
    }

    private void updateInventory() {

        synchronized (this) {
            if (hasExpired()) {
                FasitResourceWithUnmappedProperties[] fasitResources = fasitApiConsumer.fetchResources(SAML_ALIAS, BASE_URL);

                urlSamlPerEnv = asList(fasitResources).stream()
                        .filter(resource -> SAML_ALIAS.equals(resource.getAlias()) &&
                                FAGSYSTEM.equals(resource.getScope().getZone()) &&
                                isNull(resource.getScope().getApplication()) &&
                                nonNull(resource.getScope().getEnvironment()))
                        .collect(Collectors.toMap(
                                resource -> resource.getScope().getEnvironment(),
                                resource -> (String) ((Map) resource.getProperties()).get("url")));

                expiry = LocalDateTime.now().plusHours(4);
            }
        }
    }

    private boolean hasExpired() {

        return (isNull(expiry) || LocalDateTime.now().isAfter(expiry));
    }
}
