package no.nav.testnav.libs.data.pdlforvalter.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OppholdDTO extends DbVersjonDTO {

    private LocalDateTime oppholdFra;
    private LocalDateTime oppholdTil;
    private OppholdType type;

    public enum OppholdType {MIDLERTIDIG, PERMANENT, OPPLYSNING_MANGLER}
}
