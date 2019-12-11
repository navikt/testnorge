package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import java.time.ZonedDateTime;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.pdlforvalter.PdlPersonnavn;
import no.nav.dolly.domain.resultset.util.JsonZonedDateTimeDeserializer;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsPdlKontaktpersonUtenIdNummer extends PdlSomAdressat {

    @JsonDeserialize(using = JsonZonedDateTimeDeserializer.class)
    private ZonedDateTime foedselsdato;
    private PdlPersonnavn navn;
}
