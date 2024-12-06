package no.nav.testnav.libs.dto.sykemelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OrganisasjonDTO {
    private String navn;
    private String orgNr;
    private AdresseDTO adresse;
}
