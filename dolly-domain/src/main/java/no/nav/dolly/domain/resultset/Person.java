package no.nav.dolly.domain.resultset;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person {

    private Long personId;
    private String ident;
    private String identtype;
    private String kjonn;
    private String fornavn;
    private String etternavn;
    private LocalDateTime regdato;
    private List<Relasjon> relasjoner;
}