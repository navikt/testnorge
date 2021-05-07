package no.nav.pdl.forvalter.domain;

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
public class PdlKjoenn extends PdlDbVersjon {

    private Kjoenn kjoenn;

    public enum Kjoenn {
        MANN,
        KVINNE,
        UKJENT
    }
}