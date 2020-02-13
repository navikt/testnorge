package no.nav.dolly.bestilling.pensjon.domain;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpprettPerson {

    private List<String> miljo;

    private String fnr;
    private String bostedsland;
    private LocalDateTime dodsDato;
    private LocalDateTime fodselsDato;
    private LocalDateTime utvandringsDato;
}
