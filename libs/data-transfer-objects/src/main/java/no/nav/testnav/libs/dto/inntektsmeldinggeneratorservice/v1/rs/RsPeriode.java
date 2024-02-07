package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class RsPeriode {

    private LocalDate fom;
    private LocalDate tom;
}
