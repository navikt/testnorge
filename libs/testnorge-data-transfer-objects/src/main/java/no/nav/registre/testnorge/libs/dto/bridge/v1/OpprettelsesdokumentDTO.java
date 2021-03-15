package no.nav.registre.testnorge.libs.dto.bridge.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class OpprettelsesdokumentDTO {
    String miljo;
    OrganiasjonDTO organiasjon;
    String key;
}
