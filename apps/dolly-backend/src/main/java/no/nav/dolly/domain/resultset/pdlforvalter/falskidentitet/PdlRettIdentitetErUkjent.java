package no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlRettIdentitetErUkjent extends RsPdlRettIdentitet {

    private Boolean rettIdentitetErUkjent;

    @Override public String getIdentitetType() {
        return "UKJENT";
    }
}
