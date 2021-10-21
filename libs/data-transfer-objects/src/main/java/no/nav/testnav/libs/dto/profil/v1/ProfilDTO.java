package no.nav.testnav.libs.dto.profil.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ProfilDTO {
    String visningsNavn;
    String epost;
    String avdeling;
    String organisasjon;
    String type;
}
