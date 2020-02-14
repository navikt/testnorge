package no.nav.dolly.bestilling.pensjon.domain;

import java.time.LocalDate;
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

    private List<String> miljo;

    private String fnr;
    private String bostedsland;
    private LocalDate dodsDato;
    private LocalDate fodselsDato;
    private LocalDate utvandringsDato;
}
