package no.nav.registre.testnorge.synt.sykemelding.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SyntDiagnoserDTO {
    String dn;
    String s;
    String v;
}
