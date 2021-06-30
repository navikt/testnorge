package no.nav.testnav.joarkdokumentservice.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class JournalpostDTO {
    Integer journalpostId;
    String tittel;
    String temanavn;
    List<DokuemntDTO> dokumenter;
}
