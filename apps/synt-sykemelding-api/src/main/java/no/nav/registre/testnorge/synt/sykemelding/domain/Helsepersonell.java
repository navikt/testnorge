package no.nav.registre.testnorge.synt.sykemelding.domain;


import no.nav.registre.testnorge.libs.dto.helsepersonell.v1.HelsepersonellDTO;

public class Helsepersonell {
    private HelsepersonellDTO dto;

    public Helsepersonell(HelsepersonellDTO dto) {
        this.dto = dto;
    }

    public String getIdent() {
        return dto.getFnr();
    }

    public no.nav.registre.testnorge.libs.dto.sykemelding.v1.HelsepersonellDTO toDTO() {
        return no.nav.registre.testnorge.libs.dto.sykemelding.v1.HelsepersonellDTO
                .builder()
                .ident(getIdent())
                .fornavn(dto.getFornavn())
                .mellomnavn(dto.getMellomnavn())
                .etternavn(dto.getEtternavn())
                .hprId(dto.getHprId())
                .samhandlerType(dto.getSamhandlerType())
                .build();
    }
}
