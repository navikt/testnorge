package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlOpphold {

    public enum OppholdType {MIDLERTIDIG, PERMANENT, OPPLYSNING_MANGLER}

    private String kilde;
    private LocalDate oppholdFra;
    private LocalDate oppholdTil;
    private OppholdType type;
}
