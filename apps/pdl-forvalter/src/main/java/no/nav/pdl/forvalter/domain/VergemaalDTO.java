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
public class VergemaalDTO extends DbVersjonDTO {

    private String embete;
    private String sakType;
    private LocalDateTime gyldigFom;
    private LocalDateTime gyldigTom;

    private PersonRequestDTO nyVergeIdent;
    private String vergeIdent;
    private String mandatType;
}
