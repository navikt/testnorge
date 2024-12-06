package no.nav.testnav.libs.dto.sykemelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class PeriodeDTO {
    private LocalDate fom;
    private LocalDate tom;
    private AktivitetDTO aktivitet;
}
