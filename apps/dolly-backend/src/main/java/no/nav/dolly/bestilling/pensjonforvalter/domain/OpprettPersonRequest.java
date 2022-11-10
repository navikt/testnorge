package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpprettPersonRequest {

    private Set<String> miljoer;

    public Set<String> getMiljoer() {

        if (isNull(miljoer)) {
            miljoer = new HashSet<>();
        }
        return miljoer;
    }

    private String fnr;
    private String bostedsland;
    private String dodsDato;         //yyyy-MM-dd
    private String fodselsDato;      //yyyy-MM-dd
    private String utvandringsDato;  //yyyy-MM-dd
}
