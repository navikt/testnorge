package no.nav.pdl.forvalter.domain.doedsbo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.pdl.forvalter.domain.PdlPersonnavn;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlKontaktpersonUtenIdNummer implements Serializable {

    private LocalDateTime foedselsdato;
    private PdlPersonnavn navn;
}
