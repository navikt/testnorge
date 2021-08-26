package no.nav.registre.aareg.security.sts;

import lombok.RequiredArgsConstructor;
import no.nav.registre.aareg.exception.TestnorgeAaregFunctionalException;
import no.nav.registre.aareg.fasit.FasitApiConsumer;
import no.nav.registre.aareg.properties.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.registre.aareg.properties.Environment.PREPROD;
import static no.nav.registre.aareg.properties.Environment.TEST;

@Component
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

        for (var entry : urlSamlPerEnv.entrySet()) {
            if ((environment == PREPROD && (entry.getKey().contains("q"))) ||
                    (environment == TEST && (entry.getKey().contains("t")))) {
                return entry.getValue();
            }
        }

        throw new TestnorgeAaregFunctionalException(STS_NOT_FOUND);
    }

    private void updateInventory() {
        synchronized (this) {
            if (hasExpired()) {
                var fasitResources = fasitApiConsumer.fetchResources(SAML_ALIAS, BASE_URL);

                urlSamlPerEnv = Arrays.stream(fasitResources)
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
