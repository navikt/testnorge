package no.nav.testnav.joarkdokumentservice.controller.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DokumentInfoDTO {
    private Integer journalpostId;
    private Integer dokumentInfoId;
    private String tittel;
}
