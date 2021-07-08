package no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UtenlandsoppholdDTO {

    String landkode;
    PeriodeDTO periode;
    String rapporteringsperiode; // yyyy-mm
}
