package no.nav.testnav.joarkdokumentservice.controller.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AvsenderMottakerDTO {
    String type;
    String id;
    String navn;
}
