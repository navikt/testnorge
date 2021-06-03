package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.pdl.forvalter.domain.PdlDbVersjon;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsVergemaal extends PdlDbVersjon {

    private String embete;
    private String sakType;
    private LocalDateTime gyldigFom;
    private LocalDateTime gyldigTom;

    private RsPersonRequest nyVergeIdent;
    private String vergeIdent;
    private String mandatType;
}
