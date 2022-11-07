package no.nav.registre.sdforvalter.consumer.rs.request.syntetisering;

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
