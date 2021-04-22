package no.nav.pdl.forvalter.domain.falskidentitet;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.pdl.forvalter.domain.PdlKjoenn;
import no.nav.pdl.forvalter.domain.PdlPersonnavn;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsPdlRettIdentitetVedOpplysninger extends RsPdlRettIdentitet {

    private LocalDateTime foedselsdato;
    private PdlKjoenn.Kjoenn kjoenn;
    private PdlPersonnavn personnavn;
    private List<String> statsborgerskap;

    @Override
    public String getIdentitetType() {
        return "OMTRENTLIG";
    }
}
