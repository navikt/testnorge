package no.nav.registre.sdforvalter.consumer.rs.aareg.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import no.nav.testnav.libs.dto.aareg.v1.PermisjonPermittering;

import static java.util.Objects.isNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsSyntetiskPermisjon {

    private String permisjonOgPermittering;
    private String permisjonsId;
    private RsSyntetiskPeriode permisjonsPeriode;
    private Double permisjonsprosent;

    @JsonIgnore
    public PermisjonPermittering toPermisjonPermittering() {
        return PermisjonPermittering.builder()
                .permisjonPermitteringId(permisjonsId)
                .prosent(permisjonsprosent)
                .type(permisjonOgPermittering)
                .periode(isNull(permisjonsPeriode) ? null : permisjonsPeriode.toPeriode())
                .build();
    }
}
