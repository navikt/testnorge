package no.nav.testnav.apps.statusfrontend.api;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestkjoringMetrikker {

    private static final String UNKNOWN_AZP = "ukjent";
    private static final Set<String> ALLOWED_AZP_NAMES = Set.of(
            "dolly-frontend",
            "dolly-frontend-dev",
            "dolly-idporten",
            "dolly-auth-local",
            "testnav-dollystatus"
    );

    private final MeterRegistry meterRegistry;

    public void registrerStart(String type, Object principal) {
        var azp = azpFor(principal);
        meterRegistry.counter("dollystatus.testkjoring.startet", "type", type, "azp", azp).increment();
        log.info("Testkjøring startet: type={}, azp={}", type, azp);
    }

    private String azpFor(Object principal) {
        if (isNull(principal)) {
            return UNKNOWN_AZP;
        }
        if (principal instanceof Jwt jwt) {
            return azpFor(jwt);
        }
        if (principal instanceof Authentication authentication
                && authentication.getPrincipal() instanceof Jwt jwt) {
            return azpFor(jwt);
        }
        return UNKNOWN_AZP;
    }

    private String azpFor(Jwt jwt) {
        Object claim = jwt.getClaims().get("azp_name");
        if (isNull(claim) || !(claim instanceof String azpName)) {
            return UNKNOWN_AZP;
        }
        var appName = azpName.substring(azpName.lastIndexOf(':') + 1);
        return ALLOWED_AZP_NAMES.contains(appName) ? appName : UNKNOWN_AZP;
    }
}
