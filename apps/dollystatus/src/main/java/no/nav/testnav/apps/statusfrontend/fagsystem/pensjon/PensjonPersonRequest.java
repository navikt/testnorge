package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon;

import java.util.Set;

public record PensjonPersonRequest(
        Set<String> miljoer,
        String fnr,
        String fodselsDato,
        String bostedsland
) {
}
