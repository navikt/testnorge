package no.nav.testnav.apps.statusfrontend.functionaltest;

import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestResponseException.Reason;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestErrorCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionalTestErrorSanitizerTest {

    @ParameterizedTest
    @EnumSource(Reason.class)
    void shouldExposeOnlyFixedResponseFailureReasons(Reason reason) {
        var failure = new FunctionalTestResponseException(reason);
        failure.initCause(new IllegalStateException("bearer-token raw-response"));

        for (var phase : FunctionalTestErrorSanitizer.FailurePhase.values()) {
            var error = FunctionalTestErrorSanitizer.sanitize(phase, failure);

            assertThat(error.message())
                    .contains("Feilkode: " + reason.name())
                    .doesNotContain("bearer-token", "raw-response");
            assertThat(error.category()).isEqualTo(phase == FunctionalTestErrorSanitizer.FailurePhase.CLEANUP
                    ? FunctionalTestErrorCategory.CLEANUP : FunctionalTestErrorCategory.INTERNAL);
        }
    }

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
