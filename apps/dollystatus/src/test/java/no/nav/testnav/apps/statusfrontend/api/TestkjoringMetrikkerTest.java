package no.nav.testnav.apps.statusfrontend.api;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;

class TestkjoringMetrikkerTest {

    @Test
    void shouldUseAppNameForAllowListedAzpName() {
        var registry = new SimpleMeterRegistry();
        var metrikker = new TestkjoringMetrikker(registry);

        metrikker.registrerStart("automatisk", jwt("dev-gcp:dolly:dolly-frontend"));

        assertThat(count(registry, "automatisk", "dolly-frontend")).isEqualTo(1.0);
    }

    @Test
    void shouldUseUnknownForUnrecognizedAndMissingAzpName() {
        var registry = new SimpleMeterRegistry();
        var metrikker = new TestkjoringMetrikker(registry);

        metrikker.registrerStart("automatisk", jwt("dev-gcp:dolly:unknown-app"));
        metrikker.registrerStart("automatisk", jwtWithoutAzp());

        assertThat(count(registry, "automatisk", "ukjent")).isEqualTo(2.0);
    }

    @Test
    void shouldIncrementCountersPerType() {
        var registry = new SimpleMeterRegistry();
        var metrikker = new TestkjoringMetrikker(registry);
        var jwt = jwt("testnav-dollystatus");

        metrikker.registrerStart("automatisk", jwt);
        metrikker.registrerStart("automatisk", jwt);
        metrikker.registrerStart("feilede", jwt);

        assertThat(count(registry, "automatisk", "testnav-dollystatus")).isEqualTo(2.0);
        assertThat(count(registry, "feilede", "testnav-dollystatus")).isEqualTo(1.0);
    }

    private double count(SimpleMeterRegistry registry, String type, String azp) {
        return registry.get("dollystatus.testkjoring.startet")
                .tag("type", type)
                .tag("azp", azp)
                .counter()
                .count();
    }

    private Jwt jwt(String azpName) {
        return Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .claim("azp_name", azpName)
                .build();
    }

    private Jwt jwtWithoutAzp() {
        return Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .claim("sub", "test-sub")
                .build();
    }
}
