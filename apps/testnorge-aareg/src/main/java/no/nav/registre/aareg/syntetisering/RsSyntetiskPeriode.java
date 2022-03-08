package no.nav.registre.aareg.syntetisering;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
