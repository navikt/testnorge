package no.nav.testnav.libs.dto.sykemelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PeriodeDTO {

    private LocalDate fom;
    private LocalDate tom;
    private AktivitetDTO aktivitet;
    private AktivitetIkkeMuligDTO aktivitetIkkeMulig;
    private String avventendeInnspillTilArbeidsgiver;
    private Integer behandlingsdager;
    private GradertDTO gradert;
    private Boolean reisetilskudd;
}
