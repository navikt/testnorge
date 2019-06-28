package no.nav.dolly.domain.resultset;

import static java.util.Objects.isNull;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public List<Relasjon> getRelasjoner() {
        if (isNull(relasjoner)) {
            relasjoner = new ArrayList<>();
        }
        return relasjoner;
    }
}