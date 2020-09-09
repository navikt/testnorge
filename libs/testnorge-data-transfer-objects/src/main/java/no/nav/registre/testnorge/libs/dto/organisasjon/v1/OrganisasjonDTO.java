package no.nav.registre.testnorge.libs.dto.organisasjon.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class OrganisasjonDTO {
    String orgnummer;
    String enhetType;
    String navn;
    String juridiskEnhet;
    AdresseDTO postadresse;
    AdresseDTO forretningsadresser;
    String redigertnavn;
}
