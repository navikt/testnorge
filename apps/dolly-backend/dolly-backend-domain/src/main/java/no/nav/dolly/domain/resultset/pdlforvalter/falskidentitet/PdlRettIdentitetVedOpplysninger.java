package no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet;

import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.pdlforvalter.PdlPersonnavn;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlRettIdentitetVedOpplysninger {

    private LocalDate foedselsdato;
    private PdlKjoenn kjoenn;
    private PdlPersonnavn personnavn;
    private List<String> statsborgerskap;
}
