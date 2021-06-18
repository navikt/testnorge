package no.nav.pdl.forvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OppholdDTO extends DbVersjonDTO {

    public enum OppholdType {MIDLERTIDIG, PERMANENT, OPPLYSNING_MANGLER}

    private LocalDateTime oppholdFra;
    private LocalDateTime oppholdTil;
    private OppholdType type;
}
