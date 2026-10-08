package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon;

import java.util.Set;

public record TpForholdRequest(Set<String> miljoer, String fnr, String ordning) {
}
