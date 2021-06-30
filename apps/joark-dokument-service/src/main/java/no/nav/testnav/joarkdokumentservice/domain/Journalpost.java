package no.nav.testnav.joarkdokumentservice.domain;

import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.testnav.joarkdokumentservice.consumer.dto.JournalpostDTO;

@Value
public class Journalpost {
    Integer journalpostId;
    String tittel;
    String tema;
    List<DokumentInfo> dokumenter;

    public Journalpost(JournalpostDTO dto) {
        journalpostId = dto.getJournalpostId();
        tittel = dto.getTittel();
        tema = dto.getTemanavn();
        dokumenter = dto.getDokumenter() != null
                ? dto.getDokumenter().stream().map(DokumentInfo::new).collect(Collectors.toList())
                : Collections.emptyList();
    }

    public no.nav.testnav.joarkdokumentservice.controller.v2.dto.JournalpostDTO toDTO() {
        return no.nav.testnav.joarkdokumentservice.controller.v2.dto.JournalpostDTO
                .builder()
                .journalpostId(journalpostId)
                .tema(tema)
                .tittel(tittel)
                .dokumenter(dokumenter.stream().map(DokumentInfo::toDTO).collect(Collectors.toList()))
                .build();
    }
}
