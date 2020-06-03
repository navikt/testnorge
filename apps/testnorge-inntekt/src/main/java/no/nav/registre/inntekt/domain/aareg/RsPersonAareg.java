package no.nav.registre.inntekt.domain.aareg;

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
public class RsPersonAareg {

    private String ident;
    private String identtype;
}
