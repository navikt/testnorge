package no.nav.registre.testnorge.libs.dto.bridge.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class DetaljertNavnDTO {
    String navn1;
    String navn2;
    String navn3;
    String navn4;
    String navn5;
    String redigertNavn;
}
