package no.nav.testnav.joarkdokumentservice.controller.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class DokumentInfoDTO {
    Integer journalpostId;
    Integer dokumentInfoId;
    String tittel;
}
