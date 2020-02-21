package no.nav.dolly.bestilling.pensjonforvalter.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpprettPersonRequest {

    private List<String> miljoer;

    private String fnr;
    private String bostedsland;
    private String dodsDato;         //dd-MM-yyyy
    private String fodselsDato;      //dd-MM-yyyy
    private String utvandringsDato;  //dd-MM-yyyy
}
