package no.nav.registre.sigrun.domain;

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
public class SyntetiskGrunnlag {

    private String tekniskNavn;
    private String verdi;
}
