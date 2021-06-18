package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class KjoennDTO extends DbVersjonDTO {

    private Kjoenn kjoenn;

    public enum Kjoenn {
        MANN,
        KVINNE,
        UKJENT
    }
}