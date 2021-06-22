package no.nav.testnav.joarkdokumentservice.domain;

import lombok.Value;

import no.nav.testnav.joarkdokumentservice.consumer.dto.DokuemntDTO;
import no.nav.testnav.joarkdokumentservice.controller.v2.dto.DokumentInfoDTO;

@Value
public class DokumentInfo {
    Integer dokumentInfoId;
    String tittel;

    public DokumentInfo(DokuemntDTO dto) {
        dokumentInfoId = dto.getDokumentInfoId();
        tittel = dto.getTittel();
    }

    public DokumentInfoDTO toDTO(){
        return DokumentInfoDTO
                .builder()
                .dokumentInfoId(dokumentInfoId)
                .tittel(tittel)
                .build();
    }
}
