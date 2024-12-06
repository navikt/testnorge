package no.nav.testnav.libs.dto.sykemelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AdresseDTO {
    private String gate;
    private String postnummer;
    private String by;
    private String land;
}
