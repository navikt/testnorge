package no.nav.testnav.apps.syntsykemeldingapi.domain;


import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellDTO;

public class Helsepersonell {
    private HelsepersonellDTO dto;

    public Helsepersonell(HelsepersonellDTO dto) {
        this.dto = dto;
    }

    public String getIdent() {
        return dto.getFnr();
    }

    public no.nav.testnav.libs.dto.sykemelding.v1.HelsepersonellDTO toDTO() {
        return no.nav.testnav.libs.dto.sykemelding.v1.HelsepersonellDTO
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
