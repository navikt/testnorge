package no.nav.registre.sdforvalter.consumer.rs.request.syntetisering;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import no.nav.testnav.libs.dto.aareg.v1.Utenlandsopphold;

import static java.util.Objects.isNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsSyntetiskUtenlandsopphold {

    private String land;
    private RsSyntetiskPeriode periode;

    @JsonIgnore
    public Utenlandsopphold toUtenlandsopphold() {
        return Utenlandsopphold.builder()
                .landkode(land)
                .periode(isNull(periode) ? null : periode.toPeriode())
                .build();
    }
}
