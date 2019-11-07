package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlKjoenn {

    public enum Kjoenn {MANN, KVINNE, UBESTEMT}

    private String kilde;
    private Kjoenn kjoenn;
}