package no.nav.testnav.apps.statusfrontend.fagsystem.brregstub;

import java.time.LocalDate;
import java.util.List;

public record BrregstubRequest(
        String fnr,
        LocalDate fodselsdato,
        Name navn,
        Address adresse,
        List<Role> enheter,
        Integer hovedstatus,
        List<Integer> understatuser
) {

    public record Name(String navn1, String navn2, String navn3) {
    }

    public record Address(
            String adresse1,
            String adresse2,
            String adresse3,
            String postnr,
            String poststed,
            String landKode,
            String kommunenr
    ) {
    }

    public record Role(
            LocalDate registreringsdato,
            String rolle,
            String rollebeskrivelse,
            Integer orgNr,
            Name foretaksNavn,
            Address forretningsAdresse,
            Address postAdresse,
            List<RoleStatus> personRolle
    ) {
    }

    public record RoleStatus(String egenskap, boolean fratraadt) {
    }
}
