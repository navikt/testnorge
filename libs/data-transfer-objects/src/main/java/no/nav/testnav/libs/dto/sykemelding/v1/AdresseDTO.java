package no.nav.testnav.libs.dto.sykemelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
public class AdresseDTO {
    private String gate;
    private String postnummer;
    private String by;
    private String land;
}
