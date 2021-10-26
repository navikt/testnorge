package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.pdlforvalter.PdlPersonnavn;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsRsPdlKontaktpersonUtenIdNummer extends RsPdlAdressat {

    public static final String PERSON_UTENID = "PERSON_UTENID";

    private LocalDateTime foedselsdato;
    private PdlPersonnavn navn;

    @Override public String getAdressatType() {
        return PERSON_UTENID;
    }
}
