package no.nav.testnav.joarkdokumentservice.controller.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvsenderMottakerDTO {
    private String type;
    private String id;
    private String navn;
}
