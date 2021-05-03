package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlOpphold extends PdlDbVersjon {

    public enum OppholdType {MIDLERTIDIG, PERMANENT, OPPLYSNING_MANGLER}

    private String kilde;
    private LocalDateTime oppholdFra;
    private LocalDateTime oppholdTil;
    private OppholdType type;
}
