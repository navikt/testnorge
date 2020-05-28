package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdlUtflytting {

    private String kilde;
    private String tilflyttingsland;
    private String tilflyttingsstedIUtlandet;
}