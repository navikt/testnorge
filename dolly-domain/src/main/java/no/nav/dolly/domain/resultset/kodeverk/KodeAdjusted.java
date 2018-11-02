package no.nav.dolly.domain.resultset.kodeverk;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KodeAdjusted {
    private String label;
    private String value;
    private LocalDate gyldigFra;
    private LocalDate gyldigTil;
}
