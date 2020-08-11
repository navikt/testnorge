package no.nav.dolly.bestilling.sykemelding.domain;

import no.nav.dolly.bestilling.sykemelding.domain.dto.LegeDTO;

public class Lege {
    private LegeDTO dto;

    public Lege(LegeDTO dto) {
        this.dto = dto;
    }

    public String getIdent() {
        return dto.getFnr();
    }

    public LegeDTO toDTO() {
        return LegeDTO
                .builder()
                .fnr(getIdent())
                .fornavn(dto.getFornavn())
                .mellomnavn(dto.getMellomnavn())
                .etternavn(dto.getEtternavn())
                .hprId(dto.getHprId())
                .build();
    }
}

