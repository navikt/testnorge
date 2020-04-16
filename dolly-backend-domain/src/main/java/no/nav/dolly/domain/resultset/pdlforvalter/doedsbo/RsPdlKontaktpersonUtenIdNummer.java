package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.pdlforvalter.PdlPersonnavn;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsPdlKontaktpersonUtenIdNummer extends PdlSomAdressat {

    private LocalDateTime foedselsdato;
    private PdlPersonnavn navn;

    @Override public String getAdressatType() {
        return "PERSON_UTENID";
    }
}
