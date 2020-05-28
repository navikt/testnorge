package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdlInnflytting {

    private String fraflyttingsland;
    private String fraflyttingsstedIUtlandet;
    private String kilde;
}