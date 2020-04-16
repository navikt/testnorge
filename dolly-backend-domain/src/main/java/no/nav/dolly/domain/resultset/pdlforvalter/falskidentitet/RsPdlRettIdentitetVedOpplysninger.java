package no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet;

import java.time.LocalDateTime;
import java.util.List;
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
public class RsPdlRettIdentitetVedOpplysninger extends RsPdlRettIdentitet {

    private LocalDateTime foedselsdato;
    private PdlKjoenn kjoenn;
    private PdlPersonnavn personnavn;
    private List<String> statsborgerskap;

    @Override public String getIdentitetType() {
        return "OMTRENTLIG";
    }
}
