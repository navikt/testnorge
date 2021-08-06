package no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PermisjonPermitteringDTO {
    String permisjonPermitteringId;
    PeriodeDTO periode;
    Float prosent;
    String type;
}
