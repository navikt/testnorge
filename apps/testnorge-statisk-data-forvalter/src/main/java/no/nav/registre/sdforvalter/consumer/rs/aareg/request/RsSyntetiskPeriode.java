package no.nav.registre.sdforvalter.consumer.rs.aareg.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import no.nav.testnav.libs.dto.aareg.v1.Periode;

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

    @JsonIgnore
    public Periode toPeriode() {
        return Periode.builder().fom(fom).fom(tom).build();
    }
}
