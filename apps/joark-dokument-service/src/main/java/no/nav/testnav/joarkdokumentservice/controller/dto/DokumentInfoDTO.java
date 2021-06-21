package no.nav.testnav.joarkdokumentservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class DokumentInfoDTO {
    Integer journalpostId;
    Integer dokumentInfoId;
    String tittel;
}
