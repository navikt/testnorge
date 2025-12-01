package no.nav.testnav.libs.dto.pdlforvalter.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
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