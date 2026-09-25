package no.nav.testnav.apps.statusfrontend.functionaltest;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionalTestErrorSanitizerTest {

    @Test
    void shouldRemoveSensitiveValuesFromEveryFailurePhase() {
        var sensitiveError = new IllegalStateException(
                "03458537037 TEST TESTESEN bearer-token request-payload");

        for (var phase : FunctionalTestErrorSanitizer.FailurePhase.values()) {
            var error = FunctionalTestErrorSanitizer.sanitize(phase, sensitiveError);

            assertThat(error.message())
                    .doesNotContain(
                            "03458537037",
                            "TEST TESTESEN",
                            "bearer-token",
                            "request-payload");
        }
    }
}
