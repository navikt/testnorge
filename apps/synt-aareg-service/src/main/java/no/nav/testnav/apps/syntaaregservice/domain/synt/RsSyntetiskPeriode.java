package no.nav.testnav.apps.syntaaregservice.domain.synt;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsSyntetiskPeriode {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate fom;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate tom;
}
