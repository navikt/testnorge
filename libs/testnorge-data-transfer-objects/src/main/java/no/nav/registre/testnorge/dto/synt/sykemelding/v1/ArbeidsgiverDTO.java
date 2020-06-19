package no.nav.registre.testnorge.dto.synt.sykemelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidsgiverDTO {
    String navn;
    Double stillingsprosent;
    String yrkesbetegnelse;
    String gatenavn;
    String postnummer;
    String orgnr;
    String by;
    String land;
}
