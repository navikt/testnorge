package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpprettPersonRequest {

    private List<String> miljoer;

    public List<String> getMiljoer() {

        if (isNull(miljoer)) {
            miljoer = new ArrayList<>();
        }
        return miljoer;
    }

    private String fnr;
    private String bostedsland;
    private String dodsDato;         //yyyy-MM-dd
    private String fodselsDato;      //yyyy-MM-dd
    private String utvandringsDato;  //yyyy-MM-dd
}
