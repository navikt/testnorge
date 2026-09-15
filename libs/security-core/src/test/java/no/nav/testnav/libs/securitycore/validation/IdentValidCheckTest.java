package no.nav.testnav.libs.securitycore.validation;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class IdentValidCheckTest {

    @Test
    void shouldDetectValidDNumber() {
        assertThat(IdentValidCheck.isIdentValid(Set.of("41010100044"))).containsExactly("41010100044");
        assertThat(IdentValidCheck.isIdentValid("41010100044")).isTrue();
    }

    @Test
    void shouldRejectInvalidControlDigits() {
        assertThat(IdentValidCheck.isIdentValid(Set.of("41010100045"))).isEmpty();
        assertThat(IdentValidCheck.isIdentValid("41010100045")).isFalse();
    }

    @Test
    void shouldNotTreatAzureObjectIdAsPersonIdentifier() {
        assertThat(IdentValidCheck.isIdentValid(Set.of("00000000-0000-0000-0000-000000000000"))).isEmpty();
    }

    @Test
    void shouldNotTreatHashedUserIdAsPersonIdentifier() {
        assertThat(IdentValidCheck.isIdentValid(Set.of("fake-hashed-user-id"))).isEmpty();
    }

    @Test
    void shouldNotTreatNameAsPersonIdentifier() {
        assertThat(IdentValidCheck.isIdentValid(Set.of("Ola Nordmann"))).isEmpty();
    }

    @Test
    void shouldNotTreatOrganizationNumberAsPersonIdentifier() {
        assertThat(IdentValidCheck.isIdentValid(Set.of("889640782"))).isEmpty();
    }
}
