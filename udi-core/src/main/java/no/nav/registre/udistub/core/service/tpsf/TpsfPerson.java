package no.nav.registre.udistub.core.service.tpsf;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TpsfPerson {

    private Long personId;
    private String ident;
    private String identtype;
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
    private LocalDate foedselsdato;
}