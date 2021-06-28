package no.nav.registre.aareg.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PeriodeDTO {
    LocalDate fom;
    LocalDate tom;
}
