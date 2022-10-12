package no.nav.dolly.bestilling.personservice.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class AktoerIdent {

    String ident;
    Boolean historisk;
    String gruppe;
}