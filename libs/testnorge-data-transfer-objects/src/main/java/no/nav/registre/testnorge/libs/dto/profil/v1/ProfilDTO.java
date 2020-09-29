package no.nav.registre.testnorge.libs.dto.profil.v1;

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
}
