package no.nav.pdl.forvalter.domain.falskidentitet;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.pdl.forvalter.domain.PdlKjoenn;
import no.nav.pdl.forvalter.domain.PdlPersonnavn;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlRettIdentitetVedOpplysninger implements Serializable {

    private LocalDate foedselsdato;
    private PdlKjoenn.Kjoenn kjoenn;
    private PdlPersonnavn personnavn;
    private List<String> statsborgerskap;
}
