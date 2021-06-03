package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.pdl.forvalter.domain.PdlDbVersjon;
import no.nav.pdl.forvalter.domain.PdlVergemaal;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RsVergemaal extends PdlDbVersjon {

    private String embete;
    private PdlVergemaal.VergemaalType type;
    private LocalDateTime gyldigFom;
    private LocalDateTime gyldigTom;

    private RsPersonRequest nyVergeIdent;
    private String vergeIdent;
    private PdlVergemaal.Omfang omfang;
}
