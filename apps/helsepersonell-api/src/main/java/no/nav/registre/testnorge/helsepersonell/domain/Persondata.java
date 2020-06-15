package no.nav.registre.testnorge.helsepersonell.domain;

import lombok.RequiredArgsConstructor;

import no.nav.registre.testnorge.dto.hodejegeren.v1.PersondataDTO;

@RequiredArgsConstructor
public class Persondata {
    private final PersondataDTO dto;

    public String getFnr(){
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

}
