package no.nav.registre.testnorge.synt.sykemelding.domain;

import no.nav.registre.testnorge.dto.helsepersonell.v1.LegeDTO;

public class Lege {
    private LegeDTO dto;

    public Lege(LegeDTO dto) {
        this.dto = dto;
    }

    public String getIdent() {
        return dto.getFnr();
    }

    public no.nav.registre.testnorge.dto.sykemelding.v1.LegeDTO toDTO() {
        return no.nav.registre.testnorge.dto.sykemelding.v1.LegeDTO
                .builder()
                .ident(getIdent())
                .fornavn(dto.getFornavn())
                .mellomnavn(dto.getMellomnavn())
                .etternavn(dto.getEtternavn())
                .hprId(dto.getHprId())
                .build();
    }
}
