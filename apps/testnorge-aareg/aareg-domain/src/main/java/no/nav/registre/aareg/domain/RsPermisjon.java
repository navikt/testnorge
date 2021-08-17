package no.nav.registre.aareg.domain;

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
public class RsPermisjon {

    private String permisjonOgPermittering;
    private String permisjonId;
    private String beskrivelse;
    private LocalDate startdato;
    private LocalDate sluttdato;
    private Double permisjonsprosent;
}
