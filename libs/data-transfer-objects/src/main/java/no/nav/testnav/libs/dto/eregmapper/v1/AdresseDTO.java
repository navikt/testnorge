package no.nav.testnav.libs.dto.eregmapper.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AdresseDTO {
    List<String> adresser;
    String postnr;
    String kommunenr;
    String landkode;
    String poststed;
}
