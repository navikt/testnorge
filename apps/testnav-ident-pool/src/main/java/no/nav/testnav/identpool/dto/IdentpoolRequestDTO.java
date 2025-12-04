package no.nav.testnav.identpool.dto;

import no.nav.testnav.identpool.domain.Identtype;

import java.time.LocalDate;
public record IdentpoolRequestDTO(

        Identtype identtype,
        LocalDate foedtEtter,
        LocalDate foedtFoer
) {
}
