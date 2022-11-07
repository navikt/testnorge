package no.nav.registre.sdforvalter.consumer.rs.request.syntetisering;

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
