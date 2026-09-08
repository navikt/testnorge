package no.nav.testnav.apps.tenorsearchservice.domain;

public record TenorMalOwner(
        String brukerId,
        String brukernavn,
        TenorMalBrukerType brukertype
) {
}
