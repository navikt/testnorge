package no.nav.testnav.libs.reactivesessionsecurity.handler;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogoutSuccessHandlerTest {

    @Test
    void shouldKeepKnownLogoutState() {
        assertThat(LogoutSuccessHandler.normalizeLogoutState("person_org_error", "idporten"))
                .isEqualTo("person_org_error");
    }

    @Test
    void shouldUseLogoutForUnknownState() {
        assertThat(LogoutSuccessHandler.normalizeLogoutState("constructor", "idporten"))
                .isEqualTo("logout");
    }

    @Test
    void shouldUseLogoutForMissingState() {
        assertThat(LogoutSuccessHandler.normalizeLogoutState(null, "idporten"))
                .isEqualTo("logout");
    }

    @Test
    void shouldHideOrganisationErrorForAad() {
        assertThat(LogoutSuccessHandler.normalizeLogoutState("organisation_error", "aad"))
                .isEqualTo("unknown_error");
    }

    @Test
    void shouldBuildEncodedLoginUri() {
        assertThat(LogoutSuccessHandler.buildLoginUri("session_error"))
                .hasToString("/login?state=session_error");
    }
}
