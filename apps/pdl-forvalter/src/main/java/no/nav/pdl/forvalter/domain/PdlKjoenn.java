package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
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