package no.nav.testnav.joarkdokumentservice.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JournalpostDTO {

    private Integer journalpostId;
    private String tittel;
    private String temanavn;
    private String behandlingstema;
    private String behandlingstemanavn;
    private AvsenderMottakerDTO avsenderMottaker;
    private List<DokumentDTO> dokumenter;
    private SakDTO sak;
    private String journalfoerendeEnhet;
}
