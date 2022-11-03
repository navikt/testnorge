package no.nav.testnav.apps.syntaaregservice.domain.aareg;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsAktoerPerson extends RsAktoer {

    private String ident;
    private String identtype;
}
