package no.nav.registre.testnorge.libs.dto.bridge.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class OrganiasjonDTO {
    String orgnummer;
    String enhetstype;
    DetaljertNavnDTO navn;
    Boolean harAnsatte;
    String epost;
    String internettadresse;
    LocalDate stiftelsesdato;
    String mobiltelefon;
    String naeringskode;
    String maalform;
    String telefon;
    String formaal;
    List<OrganiasjonDTO> underenheter;
    AdresseDTO forretningsadresse;
    AdresseDTO postadresse;
}
