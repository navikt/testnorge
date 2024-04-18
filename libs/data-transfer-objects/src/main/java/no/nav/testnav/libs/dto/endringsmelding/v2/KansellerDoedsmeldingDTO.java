package no.nav.testnav.libs.dto.endringsmelding.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class KansellerDoedsmeldingDTO {

    String ident;
}
