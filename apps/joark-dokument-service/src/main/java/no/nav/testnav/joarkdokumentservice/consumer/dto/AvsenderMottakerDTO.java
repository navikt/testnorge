package no.nav.testnav.joarkdokumentservice.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AvsenderMottakerDTO {
    String type;
    String id;
    String navn;
}
