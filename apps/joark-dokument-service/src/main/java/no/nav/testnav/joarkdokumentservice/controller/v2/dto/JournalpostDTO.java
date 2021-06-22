package no.nav.testnav.joarkdokumentservice.controller.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class JournalpostDTO {
    Integer journalpostId;
    String tittel;
    String tema;
    List<DokumentInfoDTO> dokumenter;
}
