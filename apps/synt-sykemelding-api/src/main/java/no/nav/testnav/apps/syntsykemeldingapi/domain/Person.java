package no.nav.testnav.apps.syntsykemeldingapi.domain;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import no.nav.testnav.libs.dto.hodejegeren.v1.PersondataDTO;
import no.nav.testnav.apps.syntsykemeldingapi.util.IdentUtil;


@RequiredArgsConstructor
public class Person {
    private final PersondataDTO dto;

    public String getIdent() {
        return dto.getFnr();
    }

    public String getFornvan() {
        return dto.getFornavn();
    }

    public String getMellomnavn() {
        return dto.getMellomnavn();
    }

    public String getEtternavn() {
        return dto.getEtternavn();
    }

    /**
     * @deprecated Er et eget felt i DPL, men vi manger PDL i q1.
     */
    @Deprecated
    public LocalDate getFoedselsdato() {
        return IdentUtil.toBirthdate(getIdent());
    }
}
