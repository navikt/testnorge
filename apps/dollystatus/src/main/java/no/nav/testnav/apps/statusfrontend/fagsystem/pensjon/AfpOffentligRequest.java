package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon;

import java.time.LocalDate;
import java.util.List;

public record AfpOffentligRequest(List<String> direktekall, List<Mocksvar> mocksvar) {

    public record Mocksvar(
            String tpId,
            String fnr,
            StatusAfp statusAfp,
            LocalDate virkningsDato,
            Integer sistBenyttetG,
            List<DatoBeloep> belopsListe
    ) {
    }

    public record DatoBeloep(LocalDate fomDato, Integer belop) {
    }

    public enum StatusAfp {
        UKJENT,
        INNVILGET,
        SOKT,
        AVSLAG,
        IKKE_SOKT
    }
}
