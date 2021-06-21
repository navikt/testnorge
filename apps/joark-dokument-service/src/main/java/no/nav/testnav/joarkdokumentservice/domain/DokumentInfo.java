package no.nav.testnav.joarkdokumentservice.domain;

import no.nav.testnav.joarkdokumentservice.consumer.dto.DokuemntDTO;
import no.nav.testnav.joarkdokumentservice.controller.dto.DokumentInfoDTO;

public class DokumentInfo {
    Integer journalpostId;
    Integer dokumentInfoId;
    String tittel;

    public DokumentInfo(Integer journalpostId, DokuemntDTO dto) {
        this.journalpostId = journalpostId;
        this.dokumentInfoId = dto.getDokumentInfoId();
        this.tittel = dto.getTittel();
    }

    public DokumentInfoDTO toDTO() {
        return new DokumentInfoDTO(journalpostId, dokumentInfoId, tittel);
    }
}
