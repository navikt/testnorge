package no.nav.testnav.identpool.dto;

import no.nav.testnav.libs.dto.identpool.v1.Identtype;

import java.time.LocalDate;
public record IdentpoolRequestDTO(

        Identtype identtype,
        LocalDate foedtEtter,
        LocalDate foedtFoer
) {
}
