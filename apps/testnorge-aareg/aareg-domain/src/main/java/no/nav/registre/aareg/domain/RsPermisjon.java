package no.nav.registre.aareg.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsPermisjon {

    private String permisjonOgPermittering;
    private String permisjonId;
    private String beskrivelse;
    private RsPeriode permisjonsPeriode;
    private Double permisjonsprosent;
}
